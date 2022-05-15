package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
        val id = navigationArgs.id

        if (id > 0) {
            // todo we are editing
        } else {
            binding.saveBtn.setOnClickListener {
                addExercise()
            }
        }

        binding.apply {
            nameInput.setOnClickListener {
                nameLabel.error = null
            }

            val muscleGroups = MuscleGroup.values()
            val targetMuscleAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, muscleGroups)
            (targetMuscleLabel.editText as? AutoCompleteTextView)?.setAdapter(targetMuscleAdapter)
            targetMuscleInput.setText(MuscleGroup.QUADS.toString(), false)

            val resistanceType = ResistanceType.values()
            val equipmentAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, resistanceType)
            (equipmentLabel.editText as? AutoCompleteTextView)?.setAdapter(equipmentAdapter)
            equipmentInput.setText(ResistanceType.WEIGHT.toString(), false)
        }
    }


    private fun addExercise() {
        val name = binding.nameInput.text.toString()

        // todo maybe move logic to viewmodel
        if (name.isBlank()) {
            binding.nameLabel.error = getString(R.string.item_name_err)
            return
        }

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
}