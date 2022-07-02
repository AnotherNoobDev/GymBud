package com.example.gymbud.ui.stats

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentExerciseProgressionBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ExerciseProgressionFragment : Fragment() {
    private val navigationArgs: ExerciseProgressionFragmentArgs by navArgs()

    private val statsViewModel: StatsViewModel by activityViewModels {
        val app = activity?.application as BaseApplication
        StatsViewModelFactory(app.sessionRepository, app.exerciseTemplateRepository)
    }

    private val exerciseFiltersViewModel: ExerciseFiltersViewModel by activityViewModels {
        ExerciseFiltersViewModelFactory((activity?.application as BaseApplication).programRepository)
    }

    private val chartViewModel: ProgressionChartViewModel by activityViewModels {
        val app = activity?.application as BaseApplication
        ProgressionChartViewModelFactory(app.exerciseRepository)
    }

    private var _binding: FragmentExerciseProgressionBinding? = null
    private val binding get() = _binding!!

    private var filterByProgram: TextView? = null
    private var filterByPeriod: TextView? = null

    private var exerciseProgression: ExerciseProgression? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseProgressionBinding.inflate(inflater, container, false)

        // todo these values come from an included fragment,
        //  if I try to bind them by assigning an id to the <include/>, the parent constraint layout can't find stuff anymore.
        //  Is there a better way to do this?
        filterByProgram = binding.root.findViewById(R.id.program_value)
        filterByPeriod = binding.root.findViewById(R.id.period_value)

        binding.root.findViewById<FloatingActionButton>(R.id.edit_filters_btn).setOnClickListener {
            val action = ExerciseProgressionFragmentDirections.actionExerciseProgressionFragmentToExerciseFiltersFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            exerciseInput.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, p2, _ ->
                        selectExerciseFromOptions(p2)
                }

            when (chartViewModel.exerciseEvaluator) {
                ExerciseResultEvaluator.OneRepMax -> evaluatorSelection.check(R.id.evaluator_option_1rm)
                ExerciseResultEvaluator.MaxWeight -> evaluatorSelection.check(R.id.evaluator_option_max_weight)
            }

            evaluatorSelection.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.evaluator_option_1rm -> chartViewModel.exerciseEvaluator = ExerciseResultEvaluator.OneRepMax
                    R.id.evaluator_option_max_weight -> chartViewModel.exerciseEvaluator = ExerciseResultEvaluator.MaxWeight
                }

                updateGraph()
            }

            when (chartViewModel.timeWindow) {
                TimeWindowLength.Week -> timeWindowSelection.check(R.id.time_window_option_week)
                TimeWindowLength.Month -> timeWindowSelection.check(R.id.time_window_option_month)
                TimeWindowLength.Year -> timeWindowSelection.check(R.id.time_window_option_year)
            }

            timeWindowSelection.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.time_window_option_week -> chartViewModel.timeWindow = TimeWindowLength.Week
                    R.id.time_window_option_month -> chartViewModel.timeWindow = TimeWindowLength.Month
                    R.id.time_window_option_year -> chartViewModel.timeWindow = TimeWindowLength.Year
                }

                updateGraph()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            filterByProgram?.text = exerciseFiltersViewModel.getFilterForProgramAsText().first()
            filterByPeriod?.text = exerciseFiltersViewModel.getFilterForPeriodAsText()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            if (navigationArgs.exerciseId > 0) {
                chartViewModel.selectExercise(navigationArgs.exerciseId)
            }

            // need to set selected exercise text here before populating, otherwise it overwrites dropdown in exerciseInput :(
            val selectedExercise = chartViewModel.getSelectedExercise()
            if (selectedExercise != null) {
                binding.exerciseInput.setText(selectedExercise.name)
            }

            val exerciseOptions = chartViewModel.getExerciseOptions()

            if (exerciseOptions.isNotEmpty()) {
                val exerciseOptionsAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_list_item, exerciseOptions.map { it.name })
                binding.exerciseInput.setAdapter(exerciseOptionsAdapter)
            }

            if (selectedExercise != null) {
                updateProgression(selectedExercise)
            }
        }
    }


    private fun selectExerciseFromOptions(index: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val selectedExercise = chartViewModel.selectExercise(index)
            if (selectedExercise != null) {
                updateProgression(selectedExercise)
            }
        }
    }


    private fun updateProgression(exercise: Exercise) {
        viewLifecycleOwner.lifecycleScope.launch {
            val filters = exerciseFiltersViewModel.getFilters()
            exerciseProgression = statsViewModel.getExerciseProgression(exercise, filters)

            updateGraph()
        }
    }


    private fun updateGraph() {
        if (exerciseProgression == null) {
            return
        } else {
            exerciseProgression?.points?.forEach {
                Log.i("ExerciseProgression", it.dateMs.toString() + " " + it.resistance)
            }
        }
    }
}