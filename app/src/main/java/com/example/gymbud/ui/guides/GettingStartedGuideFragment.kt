package com.example.gymbud.ui.guides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentGettingStartedGuideBinding


class GettingStartedGuideFragment : Fragment() {
    private val navigationArgs: GettingStartedGuideFragmentArgs by navArgs()

    private var _binding: FragmentGettingStartedGuideBinding? = null
    private val binding get() = _binding!!


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
                    val action = GettingStartedGuideFragmentDirections.actionGettingStartedGuideFragmentToStartupFragment()
                    findNavController().navigate(action)
                }
            } else {
                firstTimeStartupScreenContent.visibility = View.GONE
            }
        }

        return binding.root
    }
}