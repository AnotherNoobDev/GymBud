package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.data.repository.AppRepository
import com.example.gymbud.databinding.FragmentLiveSessionExerciseBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LiveSessionExerciseFragment : Fragment() {
    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        LiveSessionViewModelFactory((activity?.application as BaseApplication).sessionRepository)
    }

    private lateinit var appRepository: AppRepository

    private var _binding: FragmentLiveSessionExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseSession: WorkoutSessionItem.ExerciseSession

    private var displayWeightUnit: WeightUnit = WeightUnit.KG


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveSessionExerciseBinding.inflate(inflater, container, false)

        setKeyboardVisibilityListener()

        return binding.root
    }


    private fun setKeyboardVisibilityListener() {
        val parentView = binding.root
        parentView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            private var alreadyVisible = false
            private val defaultKeyboardHeightDP = 100
            private val estimatedKeyboardDP = defaultKeyboardHeightDP + 48
            private val rect: Rect = Rect()

            override fun onGlobalLayout() {
                val estimatedKeyboardHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    estimatedKeyboardDP.toFloat(),
                    parentView.resources.displayMetrics
                ).toInt()

                parentView.getWindowVisibleDisplayFrame(rect)

                val heightDiff: Int = parentView.rootView.height - (rect.bottom - rect.top)
                val isVisible = heightDiff >= estimatedKeyboardHeight
                if (isVisible == alreadyVisible) {
                    return
                }

                alreadyVisible = isVisible
                onKeyboardVisibilityChanged(isVisible)
            }
        })
    }


    private fun onKeyboardVisibilityChanged(visible: Boolean) {
        if (visible) {
            // when keyboard is visible
            binding.apply {
                // hide title
                exerciseLabel.visibility = View.GONE
                exerciseTags.visibility = View.GONE

                // hide previous session section
                previousSession.visibility = View.GONE
            }
        } else {
            binding.apply {
                exerciseLabel.visibility = View.VISIBLE
                exerciseTags.visibility = View.VISIBLE

                previousSession.visibility = View.VISIBLE
            }
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository

        exerciseSession = liveSessionViewModel.getCurrentItem() as WorkoutSessionItem.ExerciseSession

        updateDisplayWeightUnit(WeightUnit.KG) // defaults

        viewLifecycleOwner.lifecycleScope.launch {
            appRepository.weightUnit.collect {
                updateDisplayWeightUnit(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            appRepository.liveSessionKeepScreenOn.collect {
                binding.root.keepScreenOn = it
            }
        }

        binding.apply {
            exerciseLabel.text = exerciseSession.getShortName()

            val intensity = exerciseSession.tags?.get(TagCategory.Intensity)?.joinToString() ?: ""
            if (intensity.isNotEmpty()) {
                exerciseTags.text = "*  $intensity *"
            }

            previousNotes.text = exerciseSession.getPreviousNotes()?: "No notes..."

            repsValue.setOnClickListener {
                repsLabel.error = null
            }

            resistanceValue.setOnClickListener {
                resistanceLabel.error = null
            }

            // TODO using different keyboard types messes up the layout...
            // resistanceValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            if (liveSessionViewModel.hasNextItem()) {
                nextItemHint.text = liveSessionViewModel.getNextItemHint()
                continueBtn.setOnClickListener {
                    proceedWithSession()
                }
            } else {
                continueBtn.text = "Finish"
                continueBtn.setOnClickListener {
                    finishSession()
                }
            }
        }
    }


    private fun updateDisplayWeightUnit(u: WeightUnit) {
        var exerciseValueStr = ""

        val prevReps = exerciseSession.getPreviousReps()
        val prevResistance = exerciseSession.getPreviousResistance()

        if (prevReps != null && prevResistance != null) {
            exerciseValueStr = "$prevReps x " + when(u) {
                WeightUnit.KG-> String.format("%.2f kg", prevResistance)
                WeightUnit.LB-> String.format("%.2f lb", convertKGtoLB(prevResistance))
            }
        }

        binding.apply {
            previousSessionValue.text = exerciseValueStr

            when(u) {
                WeightUnit.KG -> {
                    resistanceLabel.hint = "Resistance (kg)"
                }
                WeightUnit.LB -> {
                    resistanceLabel.hint = "Resistance (lb)"
                }
            }
        }

        displayWeightUnit = u
    }


    private fun recordValues(): Boolean {
        val inputReps =  binding.repsValue.text.toString().toIntOrNull()
        if (inputReps == null) {
            binding.repsLabel.error = "Please enter reps"
            return false
        }

        val inputResistance = binding.resistanceValue.text.toString().toDoubleOrNull()
        if (inputResistance == null) {
            binding.resistanceLabel.error = "Please enter resistance"
            return false
        }

        val inputResistanceKG = when (displayWeightUnit) {
            WeightUnit.KG -> inputResistance
            WeightUnit.LB -> convertLBtoKG(inputResistance)
        }

        val inputNotes = binding.notesInput.text.toString()

        exerciseSession.complete(inputReps, inputResistanceKG, inputNotes)


        return true
    }


    private fun proceedWithSession() {
        if (!recordValues()) {
            return
        }

        val action = when (liveSessionViewModel.getNextItemType()) {
            WorkoutSessionItemType.Exercise ->
                LiveSessionExerciseFragmentDirections.actionLiveSessionExerciseFragmentSelf()
            WorkoutSessionItemType.Rest ->
                LiveSessionExerciseFragmentDirections.actionLiveSessionExerciseFragmentToLiveSessionRestFragment()
            else ->
                null
        }

        if (action != null) {
            liveSessionViewModel.proceed()
            findNavController().navigate(action)
        }
    }


    private fun finishSession() {
        if (!recordValues()) {
            return
        }

        val action = LiveSessionExerciseFragmentDirections.actionLiveSessionExerciseFragmentToLiveSessionEndFragment()
        findNavController().navigate(action)
    }
}