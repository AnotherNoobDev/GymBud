package com.gymbud.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.gymbud.gymbud.BaseApplication
import com.gymbud.gymbud.databinding.FragmentStartupBinding
import com.gymbud.gymbud.ui.viewmodel.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch



class StartupFragment : Fragment() {
    private var _binding: FragmentStartupBinding? = null
    private val binding get() = _binding!!

    private val itemViewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private val appViewModel: AppViewModel by activityViewModels {
        AppViewModelFactory()
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


    private fun onFirstTimeStartup() {
        appViewModel.setAppWorkflowState(AppWorkflowState.FirstTime)
        val action = StartupFragmentDirections.actionStartupFragmentToGettingStartedGuideFragment()
        findNavController().navigate(action)
    }


    private suspend fun onReturningUserStartup() {
        appViewModel.setAppWorkflowState(AppWorkflowState.Normal)

        val withData = itemViewModel.hasData().first()

        if (withData) {
            // if we have some templates -> show Dashboard
            val action = StartupFragmentDirections.actionStartupFragmentToDashboardFragment()
            findNavController().navigate(action)
        } else {
            // if we don't have any templates -> show Templates
            val action = StartupFragmentDirections.actionStartupFragmentToTemplatesFragment()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}