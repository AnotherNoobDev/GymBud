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
import androidx.navigation.fragment.navArgs
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentExerciseDetailBinding
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import org.w3c.dom.Text

private const val TAG = "ExerciseDetail"

class ExerciseDetailFragment : Fragment() {

    private val navigationArgs: ExerciseDetailFragmentArgs by navArgs()

    private val viewModel: ExerciseViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, ExerciseViewModel.Factory(activity.application))
            .get(ExerciseViewModel::class.java)
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

        val name = view.findViewById<TextView>(R.id.name)
        name.text = exercise?.name

        val targetMuscle = view.findViewById<TextView>(R.id.target_muscle)
        targetMuscle.text = exercise?.targetMuscle.toString()

        val equipment = view.findViewById<TextView>(R.id.equipment)
        equipment.text = exercise?.resistance.toString()

        val notes = view.findViewById<TextView>(R.id.notes)
        notes.text = exercise?.description
    }
}