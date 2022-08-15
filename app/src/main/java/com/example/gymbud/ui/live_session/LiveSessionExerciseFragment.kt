package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return binding.root
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

        binding.apply {
            exerciseLabel.text = exerciseSession.getShortName()

            val intensity = exerciseSession.tags?.get(TagCategory.Intensity)?.joinToString() ?: ""
            if (intensity.isNotEmpty()) {
                exerciseTags.text = "*  $intensity *"
            }

            previousNotes.text = exerciseSession.getPreviousNotes()?: "No notes..."

            repsLabel.setOnClickListener {
                repsLabel.error = null
            }

            resistanceLabel.setOnClickListener {
                resistanceLabel.error = null
            }

            resistanceValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

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

        val inputResistanceStr = binding.resistanceValue.text.toString()
        if (inputResistanceStr.isEmpty()) {
            binding.resistanceLabel.error = "Please enter resistance"
            return false
        }

        val inputResistanceNumber = when (displayWeightUnit) {
            WeightUnit.KG -> inputResistanceStr.toDouble()
            WeightUnit.LB -> convertLBtoKG(inputResistanceStr.toDouble())
        }

        val inputNotes = binding.notesInput.text.toString()

        exerciseSession.complete(inputReps, inputResistanceNumber, inputNotes)


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