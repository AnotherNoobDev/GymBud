package com.example.gymbud.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentExerciseDetailBinding
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import com.example.gymbud.ui.viewmodel.ExerciseViewModelFactory
import org.w3c.dom.Text

private const val TAG = "ExerciseDetail"

class ExerciseDetailFragment : Fragment() {

    private val navigationArgs: ExerciseDetailFragmentArgs by navArgs()

    private val viewModel: ExerciseViewModel by activityViewModels() {
        ExerciseViewModelFactory()
    }

    private var _binding: FragmentExerciseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExerciseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id
        val exercise = viewModel.retrieveExercise(id)
        binding.apply {
            name.text = exercise?.name
            targetMuscle.text = exercise?.targetMuscle.toString()
            equipment.text = exercise?.resistance.toString()
            notes.text = exercise?.description

            editExerciseFab.setOnClickListener {
                val action = ExerciseDetailFragmentDirections.actionExerciseDetailFragmentToExerciseAddFragment(id)
                findNavController().navigate(action)
            }
        }
    }
}