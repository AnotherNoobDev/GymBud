package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.data.ExerciseRepository
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.databinding.FragmentExerciseTemplateAddBinding
import com.example.gymbud.model.ItemType
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.ui.viewmodel.ExerciseTemplateViewModel
import com.example.gymbud.ui.viewmodel.ExerciseTemplateViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ExerciseTemplateAddFragment : Fragment() {

    private val navigationArgs: ExerciseTemplateAddFragmentArgs by navArgs()

    private val viewModel: ExerciseTemplateViewModel by activityViewModels() {
        ExerciseTemplateViewModelFactory(
            (activity?.application as BaseApplication).exerciseTemplateRepository
        )
    }

    private lateinit var exerciseRepository: ExerciseRepository

    private var _binding: FragmentExerciseTemplateAddBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseTemplateAddBinding.inflate(inflater, container, false)

        exerciseRepository =  (activity?.application as BaseApplication).exerciseRepository

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            nameInput.setOnClickListener {
                nameLabel.error = null
            }
        }

        val id = navigationArgs.id

        if (id >= 0) {
            onViewCreatedWithExistingExerciseTemplate()
        } else {
            onViewCreatedWithNewExerciseTemplate()
        }
    }


    private fun onViewCreatedWithExistingExerciseTemplate() {
        val exerciseTemplate = viewModel.retrieveExerciseTemplate(navigationArgs.id)

        binding.apply {
            nameInput.setText(exerciseTemplate?.name, TextView.BufferType.SPANNABLE)
            exerciseInput.setText(exerciseTemplate?.exercise?.name, false)
            exerciseInput.isEnabled = false
            repRangeInput.values = mutableListOf<Float>(
                exerciseTemplate!!.targetRepRange.first.toFloat(),
                exerciseTemplate!!.targetRepRange.last.toFloat(),
            )

            saveBtn.setOnClickListener {
                updateExerciseTemplate()
            }

            deleteBtn.visibility = View.VISIBLE
            deleteBtn.setOnClickListener {
                deleteExerciseTemplate()
            }
        }
    }


    private fun onViewCreatedWithNewExerciseTemplate() {
        binding.apply {

            viewLifecycleOwner.lifecycleScope.launch {
                exerciseRepository.exercises.collect {
                    val exercises = it.map { ex ->
                        ex.name
                    }

                    val exerciseAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, exercises)
                    exerciseInput.setAdapter(exerciseAdapter)
                    exerciseInput.setText(exercises[0], false)
                }
            }

            saveBtn.setOnClickListener {
                addExerciseTemplate()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun addExerciseTemplate() {
        if (!validateInput()) {
            return
        }

        val name = binding.nameInput.text.toString()
        val exercise = exerciseRepository.retrieveExercise(binding.exerciseInput.text.toString())!!
        val targetRepRange = IntRange(
            binding.repRangeInput.values[0].toInt(),
            binding.repRangeInput.values[1].toInt(),
        )

        viewModel.addExerciseTemplate(
            ItemIdentifierGenerator.generateId(),
            name,
            exercise,
            targetRepRange
        )

        val action = ExerciseTemplateAddFragmentDirections.actionExerciseTemplateAddFragmentToItemListFragment(ItemType.EXERCISE_TEMPLATE)
        findNavController().navigate(action)
    }


    private fun updateExerciseTemplate() {
        if (!validateInput()) {
            return
        }

        val name = binding.nameInput.text.toString()
        val targetRepRange = IntRange(
            binding.repRangeInput.values[0].toInt(),
            binding.repRangeInput.values[1].toInt(),
        )

        viewModel.updateExerciseTemplate(
            navigationArgs.id,
            name,
            targetRepRange
        )

        val action = ExerciseTemplateAddFragmentDirections.actionExerciseTemplateAddFragmentToItemListFragment(ItemType.EXERCISE_TEMPLATE)
        findNavController().navigate(action)
    }


    private fun validateInput(): Boolean {
        // todo maybe move logic to viewmodel

        val name = binding.nameInput.text.toString()
        if (name.isBlank()) {
            binding.nameLabel.error = getString(R.string.item_name_err)
            return false
        }

        return true
    }


    private fun deleteExerciseTemplate() {
        viewModel.removeExerciseTemplate(navigationArgs.id)

        val action = ExerciseTemplateAddFragmentDirections.actionExerciseTemplateAddFragmentToItemListFragment(ItemType.EXERCISE_TEMPLATE)
        findNavController().navigate(action)
    }
}