package com.example.gymbud.ui.live_session

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.data.repository.AppRepository
import com.example.gymbud.databinding.FragmentLiveSessionEndBinding
import com.example.gymbud.ui.SessionExerciseListRecyclerViewAdapter
import com.example.gymbud.ui.viewmodel.LiveSessionViewModel
import com.example.gymbud.ui.viewmodel.LiveSessionViewModelFactory
import com.example.gymbud.utility.TimeFormatter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LiveSessionEndFragment : Fragment() {
    private val liveSessionViewModel: LiveSessionViewModel by activityViewModels {
        LiveSessionViewModelFactory((activity?.application as BaseApplication).sessionRepository)
    }

    private lateinit var appRepository: AppRepository

    private var _binding: FragmentLiveSessionEndBinding? = null
    private val binding get() = _binding!!

    private val sessionResultsAdapter = SessionExerciseListRecyclerViewAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        liveSessionViewModel.finish()

        _binding = FragmentLiveSessionEndBinding.inflate(inflater, container, false)
        binding.resultsRecyclerView.adapter = sessionResultsAdapter

        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository

        viewLifecycleOwner.lifecycleScope.launch {
            appRepository.weightUnit.collect {
                sessionResultsAdapter.displayWeightUnit = it
            }
        }

        val results = liveSessionViewModel.getResults()
        sessionResultsAdapter.submitList(results)

        binding.apply {
            durationValue.text = TimeFormatter.getFormattedTimeHHMMSS(liveSessionViewModel.getDuration() / 1000)

            if (results.isEmpty()) {
                notesInput.isEnabled = false
                continueBtn.visibility = View.GONE
                discardBtn.text = "Exit"
            }

            continueBtn.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch{
                    liveSessionViewModel.saveSession(notesInput.text.toString())
                    val action = LiveSessionEndFragmentDirections.actionLiveSessionEndFragmentToDashboardFragment()
                    findNavController().navigate(action)
                }
            }

            discardBtn.setOnClickListener {
                liveSessionViewModel.discardSession()
                val action = LiveSessionEndFragmentDirections.actionLiveSessionEndFragmentToDashboardFragment()
                findNavController().navigate(action)
            }
        }
    }
}