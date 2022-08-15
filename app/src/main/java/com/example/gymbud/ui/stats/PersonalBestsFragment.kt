package com.example.gymbud.ui.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.data.repository.AppRepository
import com.example.gymbud.databinding.FragmentPersonalBestsBinding
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.ui.viewmodel.ExerciseFiltersViewModel
import com.example.gymbud.ui.viewmodel.ExerciseFiltersViewModelFactory
import com.example.gymbud.ui.viewmodel.StatsViewModel
import com.example.gymbud.ui.viewmodel.StatsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class PersonalBestsFragment : Fragment() {
    private val statsViewModel: StatsViewModel by activityViewModels {
        val app = activity?.application as BaseApplication
        StatsViewModelFactory(app.sessionRepository, app.exerciseTemplateRepository)
    }

    private val exerciseFiltersViewModel: ExerciseFiltersViewModel by activityViewModels {
        ExerciseFiltersViewModelFactory((activity?.application as BaseApplication).programRepository)
    }

    private lateinit var appRepository: AppRepository

    private var _binding: FragmentPersonalBestsBinding? = null
    private val binding get() = _binding!!

    private var filterByProgram: TextView? = null
    private var filterByPeriod: TextView? = null

    private val personalBestsAdapter = PersonalBestsRecyclerViewAdapter { exerciseId, sessionDate ->
        onPersonalBestClicked(exerciseId, sessionDate)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBestsBinding.inflate(inflater, container, false)

        // todo these values come from an included fragment,
        //  if I try to bind them by assigning an id to the <include/>, the parent constraint layout can't find stuff anymore.
        //  Is there a better way to do this?
        filterByProgram = binding.root.findViewById(R.id.program_value)
        filterByPeriod = binding.root.findViewById(R.id.period_value)

        binding.root.findViewById<FloatingActionButton>(R.id.edit_filters_btn).setOnClickListener {
            val action = PersonalBestsFragmentDirections.actionPersonalBestsFragmentToExerciseFiltersFragment()
            findNavController().navigate(action)
        }

        binding.personalBestsGrid.adapter = personalBestsAdapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository

        viewLifecycleOwner.lifecycleScope.launch {
            appRepository.weightUnit.collect {
                personalBestsAdapter.displayWeightUnit = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            filterByProgram?.text = exerciseFiltersViewModel.getFilterForProgramAsText().first()
            filterByPeriod?.text =  exerciseFiltersViewModel.getFilterForPeriodAsText()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val filters = exerciseFiltersViewModel.getFilters()
            personalBestsAdapter.submitList(statsViewModel.getPersonalBests(filters))
        }
    }


    private fun onPersonalBestClicked(exerciseId: ItemIdentifier, sessionDate: Long) {
        val action = PersonalBestsFragmentDirections.actionPersonalBestsFragmentToExerciseProgressionFragment(exerciseId, sessionDate)
        findNavController().navigate(action)
    }
}