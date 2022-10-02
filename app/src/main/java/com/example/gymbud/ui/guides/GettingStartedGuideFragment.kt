package com.example.gymbud.ui.guides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentGettingStartedGuideBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.launch


class GettingStartedGuideFragment : Fragment() {
    private val navigationArgs: GettingStartedGuideFragmentArgs by navArgs()

    private var _binding: FragmentGettingStartedGuideBinding? = null
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
        _binding = FragmentGettingStartedGuideBinding.inflate(inflater, container, false)

        binding.apply {

            templatesGuide.text = "\n" + getString(R.string.templatesGuideLabel) + "."
            templatesGuide.setOnClickListener {
                val action = GettingStartedGuideFragmentDirections.actionGettingStartedGuideFragmentToCreatingProgramGuideFragment()
                findNavController().navigate(action)
            }

            liveSessionGuide.text = "\n" + getString(R.string.liveSessionGuideLabel) + "."
            liveSessionGuide.setOnClickListener {
                val action = GettingStartedGuideFragmentDirections.actionGettingStartedGuideFragmentToTrackingWorkoutGuideFragment()
                findNavController().navigate(action)
            }

            statsGuide.text = "\n" + getString(R.string.statsGuideLabel) + "."
            statsGuide.setOnClickListener {
                val action = GettingStartedGuideFragmentDirections.actionGettingStartedGuideFragmentToMonitoringProgressGuideFragment()
                findNavController().navigate(action)
            }

            if (navigationArgs.isFirstTimeStartupScreen) {
                firstTimeStartupScreenContent.visibility = View.VISIBLE
                continueBtn.setOnClickListener {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.populateWithMinimum()

                        val appRepo = (activity?.application as BaseApplication).appRepository
                        appRepo.updateFirstTimeStart(false)

                        val action = GettingStartedGuideFragmentDirections.actionGettingStartedGuideFragmentToStartupFragment()
                        findNavController().navigate(action)
                    }
                }
            } else {
                firstTimeStartupScreenContent.visibility = View.GONE
            }
        }

        return binding.root
    }
}