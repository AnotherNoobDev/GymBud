package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentDashboardBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemViewModel by activityViewModels() {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.apply {
            templatesButton.setOnClickListener {
                val action =  DashboardFragmentDirections.actionDashboardFragmentToProgramBuilderFragment()
                findNavController().navigate(action)
            }

            resetDbButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        viewModel.removeAll()
                    }
                    val action = DashboardFragmentDirections.actionDashboardFragmentToStartupFragment()
                    findNavController().navigate(action)
                }
            }
        }

        return binding.root
    }
}