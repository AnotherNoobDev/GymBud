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
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentExerciseDetailBinding
import com.example.gymbud.databinding.FragmentExerciseTemplateDetailBinding
import com.example.gymbud.ui.viewmodel.ExerciseTemplateViewModel
import com.example.gymbud.ui.viewmodel.ExerciseTemplateViewModelFactory
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import com.example.gymbud.ui.viewmodel.ExerciseViewModelFactory
import org.w3c.dom.Text

class ExerciseTemplateDetailFragment : Fragment() {

    private val navigationArgs: ExerciseTemplateDetailFragmentArgs by navArgs()

    private val viewModel: ExerciseTemplateViewModel by activityViewModels() {
        ExerciseTemplateViewModelFactory(
            (activity?.application as BaseApplication).exerciseTemplateRepository
        )
    }

    private var _binding: FragmentExerciseTemplateDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExerciseTemplateDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id
        val exerciseTemplate = viewModel.retrieveExerciseTemplate(id)
        binding.apply {
            name.text = exerciseTemplate?.name
            exercise.text = exerciseTemplate?.exercise?.name // todo why does this appear as potentially null?!
            repRange.text = exerciseTemplate?.targetRepRange.toString() + " reps"

            editFab.setOnClickListener {
                val action = ExerciseTemplateDetailFragmentDirections
                    .actionExerciseTemplateDetailFragmentToExerciseTemplateAddFragment(id)
                findNavController().navigate(action)
            }
        }
    }
}