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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProgramBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            viewExercisesButton.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToAddItemFragment(ItemType.EXERCISE)
                findNavController().navigate(action)
            }

            viewExerciseTemplatesButton.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToAddItemFragment(ItemType.EXERCISE_TEMPLATE)
                findNavController().navigate(action)
            }

            viewSetTemplatesButton.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToAddItemFragment(ItemType.SET_TEMPLATE)
                findNavController().navigate(action)
            }

            viewWorkoutTemplatesButton.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToAddItemFragment(ItemType.WORKOUT_TEMPLATE)
                findNavController().navigate(action)
            }

            viewProgramsButton.setOnClickListener {
                val action = ProgramBuilderFragmentDirections.actionProgramBuilderFragmentToAddItemFragment(ItemType.PROGRAM_TEMPLATE)
                findNavController().navigate(action)
            }
        }
    }
}