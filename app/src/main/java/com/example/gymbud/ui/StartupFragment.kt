package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentStartupBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



class StartupFragment : Fragment() {
    private var _binding: FragmentStartupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // determine what startup screen we want to show
        viewLifecycleOwner.lifecycleScope.launch {
            val appRepo = (activity?.application as BaseApplication).appRepository
            val isFirstTimeStartup =  appRepo.firstTimeStart.first()
            if (isFirstTimeStartup) {
                onFirstTimeStartup()
            } else {
                onReturningUserStartup()
            }
        }
    }


    private suspend fun onFirstTimeStartup() {
        val appRepo = (activity?.application as BaseApplication).appRepository
        appRepo.updateFirstTimeStart(false)

        val action = StartupFragmentDirections.actionStartupFragmentToGettingStartedGuideFragment()
        findNavController().navigate(action)
    }


    private suspend fun onReturningUserStartup() {
        viewModel.hasData().collect { withData ->
            if (withData) {
                // if we have some templates -> show Dashboard
                val action = StartupFragmentDirections.actionStartupFragmentToDashboardFragment()
                findNavController().navigate(action)
            } else {
                // if we don't have any templates -> show Templates
                val action = StartupFragmentDirections.actionStartupFragmentToTemplatesFragment()
                binding.root.findNavController().navigate(action)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}