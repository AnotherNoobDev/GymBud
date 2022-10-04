package com.gymbud.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gymbud.gymbud.databinding.FragmentProgramBuilderBinding
import com.gymbud.gymbud.model.ItemType

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
            exercisesPanel.description.text = "An Exercise describes the movement that shall be performed and the (main) muscle group it targets."
            exercisesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.EXERCISE)
                findNavController().navigate(action)
            }

            exerciseTemplatesPanel.title.text = "Exercise Templates"
            exerciseTemplatesPanel.description.text = "An Exercise Template is an Exercise with a specified rep range. Exercise Templates are the building blocks for Set Templates."
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
            setTemplatesPanel.description.text = "A Set Template is composed of Exercise Templates and Rest Periods. Set Templates are the building blocks for Workout Templates."
            setTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.SET_TEMPLATE)
                findNavController().navigate(action)
            }

            workoutTemplatesPanel.title.text = "Workout Templates"
            workoutTemplatesPanel.description.text = "A Workout Template is composed of Sets and Rest Periods. Workout Templates are the building blocks for Programs."
            workoutTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.WORKOUT_TEMPLATE)
                findNavController().navigate(action)
            }

            programTemplatesPanel.title.text = "Programs"
            programTemplatesPanel.description.text = "A Program is a repeatable training block that spans a given number of days. Each day in a Program is either a Workout or a Rest Day."
            programTemplatesPanel.card.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToItemListFragment(ItemType.PROGRAM_TEMPLATE)
                findNavController().navigate(action)
            }
        }
    }
}