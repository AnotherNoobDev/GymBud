package com.example.gymbud.ui.stats

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentStatsSessionCalendarBinding
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.ui.viewmodel.StatsViewModel
import com.example.gymbud.ui.viewmodel.StatsViewModelFactory
import com.example.gymbud.utility.OnSwipeTouchListener
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class StatsSessionCalendarFragment : Fragment() {
    private val statsViewModel: StatsViewModel by activityViewModels {
        val app = activity?.application as BaseApplication
        StatsViewModelFactory(app.sessionRepository, app.exerciseTemplateRepository)
    }

    private var _binding: FragmentStatsSessionCalendarBinding? = null
    private val binding get() = _binding!!

    private val sessionCalendarAdapter = SessionCalendarRecyclerViewAdapter {
        onSessionClicked(it)
    }

    private val calendar = Calendar.getInstance()

    // we always want it formatted like this, regardless of device locale
    private val monthYearDateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsSessionCalendarBinding.inflate(inflater, container, false)
        binding.daysOfTheMonthRecyclerView.adapter = sessionCalendarAdapter
        binding.daysOfTheMonthRecyclerView.layoutManager = object:  GridLayoutManager(requireContext(), 7) {
            override fun canScrollVertically(): Boolean { return false }
        }

        val onSwipeListener = object: OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                calendar.add(Calendar.MONTH, 1)
                updateCalendar()
            }

            override fun onSwipeRight() {
                calendar.add(Calendar.MONTH, -1)
                updateCalendar()
            }
        }

        // todo how to handle this regardless of where it happens on the fragment?
        // todo check on actual device.. on emulator it feels bad.. maybe just replace with left right arrows next to name as an easy alternative
        binding.daysOfTheMonthRecyclerView.setOnTouchListener(onSwipeListener)
        binding.root.setOnTouchListener(onSwipeListener)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateCalendar()
    }


    private fun updateCalendar() {
        binding.apply {
            monthAndYearLabel.text = monthYearDateFormat.format(calendar.time)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val sessions = statsViewModel.getSessionsByMonth(calendar[Calendar.YEAR], calendar[Calendar.MONTH], 42)
            sessionCalendarAdapter.submitList(sessions)
        }
    }


    private fun onSessionClicked(sessionId: ItemIdentifier) {
        val action = StatsSessionCalendarFragmentDirections.actionStatsSessionCalendarFragmentToSessionDetailFragment(sessionId)
        findNavController().navigate(action)
    }
}