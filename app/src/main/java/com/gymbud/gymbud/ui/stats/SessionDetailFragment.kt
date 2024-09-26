package com.gymbud.gymbud.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.databinding.FragmentSessionDetailBinding
import com.gymbud.gymbud.model.WorkoutSession
import com.gymbud.gymbud.ui.SessionExerciseListRecyclerViewAdapter
import com.gymbud.gymbud.ui.viewmodel.StatsViewModel
import com.gymbud.gymbud.ui.viewmodel.StatsViewModelFactory
import com.gymbud.gymbud.utility.TimeFormatter
import kotlinx.coroutines.launch


class SessionDetailFragment : Fragment() {
    private val navigationArgs: SessionDetailFragmentArgs by navArgs()

    private val statsViewModel: StatsViewModel by activityViewModels {
        val app = activity?.application as BaseApplication
        StatsViewModelFactory(app.sessionRepository, app.exerciseTemplateRepository)
    }

    private lateinit var appRepository: AppRepository

    private var _binding: FragmentSessionDetailBinding? = null
    private val binding get() = _binding!!

    private val sessionAdapter = SessionExerciseListRecyclerViewAdapter(showProgression = false, showNotes = true)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionDetailBinding.inflate(inflater, container, false)
        binding.apply{
            resultsRecyclerView.adapter = sessionAdapter

            deleteBtn.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Workout Session?")
                    .setPositiveButton("Ok") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            statsViewModel.removeSession(navigationArgs.id)
                            findNavController().navigateUp()
                        }
                    }
                    .setNegativeButton("Cancel") {_,_ ->
                    }
                    .show()
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository

        viewLifecycleOwner.lifecycleScope.launch {
            appRepository.weightUnit.collect {
                sessionAdapter.displayWeightUnit = it
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val session = statsViewModel.getSession(navigationArgs.id)
            if (session != null) {
                updateSession(session)
            }
        }
    }


    private fun updateSession(session: WorkoutSession) {
        binding.apply {
            workoutLabel.text = session.getShortName()
            dateValue.text = TimeFormatter.getFormattedDateDDMMYYYY(session.getStartTime())
            durationValue.text = TimeFormatter.getFormattedTimeHHMMSS(session.getDuration() / 1000)
            sessionAdapter.submitList(session.getExerciseSessions())
            notesValue.text = session.notes
        }
    }
}