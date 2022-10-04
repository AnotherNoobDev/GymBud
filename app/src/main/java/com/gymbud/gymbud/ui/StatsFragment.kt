package com.gymbud.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.gymbud.gymbud.databinding.FragmentStatsBinding


class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        binding.apply {
            personalBestsPanel.title.text="Personal Bests"
            personalBestsPanel.description.text="Your best results for each Exercise."
            personalBestsPanel.card.setOnClickListener {
                val action = StatsFragmentDirections.actionStatsFragmentToPersonalBestsFragment()
                findNavController().navigate(action)
            }

            progressionChartsPanel.title.text="Progression Charts"
            progressionChartsPanel.description.text="See how you progressed over time for each Exercise"
            progressionChartsPanel.card.setOnClickListener {
                val action = StatsFragmentDirections.actionStatsFragmentToExerciseProgressionFragment()
                findNavController().navigate(action)
            }

            sessionHistoryPanel.title.text="Session History"
            sessionHistoryPanel.description.text="Lookup a specific session in the calendar."
            sessionHistoryPanel.card.setOnClickListener {
                val action = StatsFragmentDirections.actionStatsFragmentToStatsSessionCalendarFragment()
                findNavController().navigate(action)
            }
        }

        return binding.root
    }
}