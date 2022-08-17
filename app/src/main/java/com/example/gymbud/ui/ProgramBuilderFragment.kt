package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gymbud.databinding.FragmentProgramBuilderBinding
import com.example.gymbud.model.ItemType

class ProgramBuilderFragment : Fragment() {
    private var _binding: FragmentProgramBuilderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProgramBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            exercisesPanel.title.text = "Exercises"
            exercisesPanel.description.text = "Descriptions of movements to target specific muscles."
            exercisesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.EXERCISE)
                findNavController().navigate(action)
            }

            exerciseTemplatesPanel.title.text = "Exercise Templates"
            exerciseTemplatesPanel.description.text = "Customizations on top of Exercises, for example rep. ranges."
            exerciseTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.EXERCISE_TEMPLATE)
                findNavController().navigate(action)
            }

            restPeriodsPanel.title.text = "Rest Periods"
            restPeriodsPanel.description.text = "Amount of time to rest between Exercises or Sets."
            restPeriodsPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.REST_PERIOD)
                findNavController().navigate(action)
            }

            setTemplatesPanel.title.text = "Set Templates"
            setTemplatesPanel.description.text = "Group of ExerciseTemplates and RestPeriods."
            setTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.SET_TEMPLATE)
                findNavController().navigate(action)
            }

            workoutTemplatesPanel.title.text = "Workout Templates"
            workoutTemplatesPanel.description.text = "Group of Sets and RestPeriods"
            workoutTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.WORKOUT_TEMPLATE)
                findNavController().navigate(action)
            }

            programTemplatesPanel.title.text = "Programs"
            programTemplatesPanel.description.text = "Group of Workouts."
            programTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.PROGRAM_TEMPLATE)
                findNavController().navigate(action)
            }
        }
    }
}