package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.AppConfig
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.data.repository.AppRepository
import com.example.gymbud.databinding.FragmentLiveSessionExerciseBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import com.example.gymbud.utility.SoftKeyboardVisibilityListener
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val TAG = "LiveSesExerciseFragment"


class LiveSessionExerciseFragment : Fragment() {
    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        LiveSessionViewModelFactory(
            (activity?.application as BaseApplication).sessionRepository,
            (activity?.application as BaseApplication).appRepository,
        )
    }

    private lateinit var appRepository: AppRepository

    private var _binding: FragmentLiveSessionExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseSession: WorkoutSessionItem.ExerciseSession

    private var displayWeightUnit: WeightUnit = WeightUnit.KG

    private lateinit var keyboardVisibilityListener: SoftKeyboardVisibilityListener

    private var youtubePlayer: YouTubePlayer? = null
    private var videoId: String? = null

    private var showInstructions = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLiveSessionExerciseBinding.inflate(inflater, container, false)

        keyboardVisibilityListener = SoftKeyboardVisibilityListener(binding.root) {
            onKeyboardVisibilityChanged(it)
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardVisibilityListener)

        setupYoutubePlayer()

        return binding.root
    }


    private fun onKeyboardVisibilityChanged(visible: Boolean) {
        if (visible) {
            // when keyboard is visible
            binding.apply {
                // hide title
                exerciseLabel.visibility = View.GONE
                exerciseTags.visibility = View.GONE

                // hide info
                infoContainer.visibility = View.GONE
            }
        } else {
            binding.apply {
                exerciseLabel.visibility = View.VISIBLE
                exerciseTags.visibility = View.VISIBLE

                infoContainer.visibility = View.VISIBLE
            }
        }
    }


    private fun setupYoutubePlayer() {
        val youTubePlayerFragment = YouTubePlayerSupportFragmentX.newInstance()

        youTubePlayerFragment.initialize(
            AppConfig.youtubeApiKey,
            object : YouTubePlayer.OnInitializedListener {

                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    player: YouTubePlayer, b: Boolean
                ) {
                    youtubePlayer = player

                    youtubePlayer!!.setShowFullscreenButton(false)

                    cueYoutubePlayerVideo()
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    err: YouTubeInitializationResult
                ) {
                    Log.e(TAG, "Failed to initialize YoutubePlayer: $err")
                }
            }
        )

        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.youtubePlayerPlaceholder, youTubePlayerFragment).commit()
    }


    private fun cueYoutubePlayerVideo() {
        if (youtubePlayer == null || videoId == null) {
            return
        }

        youtubePlayer!!.cueVideo(videoId!!)
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

            // instructions
            exerciseNotes.text = exerciseSession.exerciseTemplate.exercise.notes
            if (exerciseSession.exerciseTemplate.exercise.videoTutorial != "") {
                youtubePlayerPlaceholder.visibility = View.VISIBLE
                videoId = exerciseSession.exerciseTemplate.exercise.videoTutorial
                cueYoutubePlayerVideo()
            } else {
                youtubePlayerPlaceholder.visibility = View.GONE
                videoId = null
            }

            updateInstructionsVisibility()
            expandCollapseInstructions.setOnClickListener {
                showInstructions = !showInstructions
                updateInstructionsVisibility()
            }

            previousNotes.text = exerciseSession.getPreviousNotes()?: "No notes..."

            repsValue.setOnClickListener {
                repsLabel.error = null
            }

            resistanceValue.setOnClickListener {
                resistanceLabel.error = null
            }

            repsValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            resistanceValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

            if (liveSessionViewModel.hasPreviousItem()) {
                previousBtn.setOnClickListener {
                    goBackInSession()
                }
            } else {
                previousBtn.visibility = View.GONE
            }

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

            if (exerciseSession.isCompleted) {
                repsValue.setText(exerciseSession.actualReps.toString(), TextView.BufferType.SPANNABLE)

                when(u) {
                    WeightUnit.KG -> {
                        resistanceValue.setText(exerciseSession.actualResistance.toString(), TextView.BufferType.SPANNABLE)
                    }
                    WeightUnit.LB -> {
                        resistanceValue.setText(convertKGtoLB(exerciseSession.actualResistance).toString(), TextView.BufferType.SPANNABLE)
                    }
                }

                notesInput.setText(exerciseSession.notes, TextView.BufferType.SPANNABLE)
            }
        }

        displayWeightUnit = u
    }


    private fun updateInstructionsVisibility() {
        binding.apply {
            if (showInstructions) {
                instructionsContent.visibility = View.VISIBLE
                expandCollapseInstructions.setImageResource(R.drawable.ic_expanded_24)
            } else {
                instructionsContent.visibility = View.GONE
                expandCollapseInstructions.setImageResource(R.drawable.ic_collapsed_24)
            }
        }
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


    private fun goBackInSession() {
        val action = when (liveSessionViewModel.getPreviousItemType()) {
            WorkoutSessionItemType.Exercise ->
                LiveSessionExerciseFragmentDirections.actionLiveSessionExerciseFragmentSelf()
            WorkoutSessionItemType.Rest ->
                LiveSessionExerciseFragmentDirections.actionLiveSessionExerciseFragmentToLiveSessionRestFragment()
            else ->
                null
        }

        if (action != null) {
            liveSessionViewModel.goBack()
            findNavController().navigate(action)
        }
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


    override fun onStart() {
        super.onStart()

        // set keyboard mode to adjust resize to ensure all fields all visible on a single screen
        Log.d("keyboard_mode", "adjust resize")
        @Suppress("DEPRECATION")
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }


    override fun onStop() {
        super.onStop()

        // return keyboard mode to previous val
        Log.d("keyboard_mode", "adjust nothing")
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardVisibilityListener)
    }
}