package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentLiveSessionExerciseBinding
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.model.WorkoutSessionItem
import com.example.gymbud.model.WorkoutSessionItemType
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory

// todo display tags

class LiveSessionExerciseFragment : Fragment() {
    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        LiveSessionViewModelFactory((activity?.application as BaseApplication).sessionRepository)
    }

    private var _binding: FragmentLiveSessionExerciseBinding? = null
    private val binding get() = _binding!!

    private lateinit var exerciseSession: WorkoutSessionItem.ExerciseSession

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

        exerciseSession = liveSessionViewModel.getCurrentItem() as WorkoutSessionItem.ExerciseSession

        binding.apply {
            exerciseLabel.text = exerciseSession.getShortName()
            previousSessionValue.text = exerciseSession.getPreviousResult()?: "-"
            previousNotes.text = exerciseSession.getPreviousNotes()?: "No notes..."

            repsLabel.setOnClickListener {
                repsLabel.error = null
            }

            resistanceLabel.setOnClickListener {
                resistanceLabel.error = null
            }

            if (exerciseSession.exerciseTemplate.exercise.resistance == ResistanceType.WEIGHT) {
                resistanceValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
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


    private fun recordValues(): Boolean {
        val inputReps =  binding.repsValue.text.toString().toIntOrNull()
        if (inputReps == null) {
            binding.repsLabel.error = "Please enter reps"
            return false
        }

        val inputResistance = binding.resistanceValue.text.toString()
        if (inputResistance.isEmpty()) {
            binding.resistanceLabel.error = "Please enter resistance"
            return false
        }

        val inputNotes = binding.notesInput.text.toString()

        exerciseSession.complete(inputReps, inputResistance, inputNotes)


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