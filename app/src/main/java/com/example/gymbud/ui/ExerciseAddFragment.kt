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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.R
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.databinding.FragmentExerciseAddBinding
import com.example.gymbud.databinding.FragmentExerciseDetailBinding
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import com.example.gymbud.ui.viewmodel.ExerciseViewModelFactory


class ExerciseAddFragment : Fragment() {

    private val navigationArgs: ExerciseAddFragmentArgs by navArgs()

    private val viewModel: ExerciseViewModel by activityViewModels() {
        ExerciseViewModelFactory()
    }

    private var _binding: FragmentExerciseAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExerciseAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            nameInput.setOnClickListener {
                nameLabel.error = null
            }

            val muscleGroups = MuscleGroup.values()
            val targetMuscleAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, muscleGroups)
            (targetMuscleLabel.editText as? AutoCompleteTextView)?.setAdapter(targetMuscleAdapter)


            val resistanceType = ResistanceType.values()
            val equipmentAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, resistanceType)
            (equipmentLabel.editText as? AutoCompleteTextView)?.setAdapter(equipmentAdapter)

        }

        val id = navigationArgs.id

        if (id > 0) {
            onViewCreatedWithExistingExercise()
        } else {
            onViewCreatedWithNewExercise()
        }
    }


    private fun onViewCreatedWithExistingExercise() {
        val exercise = viewModel.retrieveExercise(navigationArgs.id)

        binding.apply {
            nameInput.setText(exercise?.name, TextView.BufferType.SPANNABLE)
            targetMuscleInput.setText(exercise?.targetMuscle.toString(), false)
            equipmentInput.setText(exercise?.resistance.toString(), false)
            notesInput.setText(exercise?.description, TextView.BufferType.SPANNABLE)

            saveBtn.setOnClickListener {
                updateExercise()
            }

            deleteBtn.visibility = View.VISIBLE
            deleteBtn.setOnClickListener {
                deleteExercise()
            }
        }
    }


    private fun onViewCreatedWithNewExercise() {
        binding.apply {
            targetMuscleInput.setText(MuscleGroup.QUADS.toString(), false)
            equipmentInput.setText(ResistanceType.WEIGHT.toString(), false)

            saveBtn.setOnClickListener {
                addExercise()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun addExercise() {
        if (!validateInput()) {
            return
        }

        val name = binding.nameInput.text.toString()
        val resistance = ResistanceType.valueOf(binding.equipmentInput.text.toString())
        val targetMuscle = MuscleGroup.valueOf(binding.targetMuscleInput.text.toString())
        val notes = binding.notesInput.text.toString()

        viewModel.addExercise(
            ItemIdentifierGenerator.generateId(),
            name,
            resistance,
            targetMuscle,
            notes
        )

        val action = ExerciseAddFragmentDirections.actionExerciseAddFragmentToAddItemFragment()
        findNavController().navigate(action)
    }


    // todo seems kinda duplicate with addExercise
    private fun updateExercise() {
        if (!validateInput()) {
            return
        }

        val name = binding.nameInput.text.toString()
        val resistance = ResistanceType.valueOf(binding.equipmentInput.text.toString())
        val targetMuscle = MuscleGroup.valueOf(binding.targetMuscleInput.text.toString())
        val notes = binding.notesInput.text.toString()

        viewModel.updateExercise(
            navigationArgs.id,
            name,
            resistance,
            targetMuscle,
            notes
        )

        val action = ExerciseAddFragmentDirections.actionExerciseAddFragmentToAddItemFragment()
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


    private fun deleteExercise() {
        viewModel.removeExercise(navigationArgs.id)

        val action = ExerciseAddFragmentDirections.actionExerciseAddFragmentToAddItemFragment()
        findNavController().navigate(action)
    }
}