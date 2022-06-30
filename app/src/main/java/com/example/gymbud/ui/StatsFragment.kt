package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gymbud.databinding.FragmentStatsBinding


class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        binding.apply {
            personalBestsBtn.setOnClickListener {
                val action = StatsFragmentDirections.actionStatsFragmentToPersonalBestsFragment()
                findNavController().navigate(action)
            }

            byExerciseBtn.setOnClickListener {
                // todo
            }

            bySessionBtn.setOnClickListener {
                val action = StatsFragmentDirections.actionStatsFragmentToStatsSessionCalendarFragment()
                findNavController().navigate(action)
            }
        }

        return binding.root
    }
}