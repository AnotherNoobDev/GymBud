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
import com.example.gymbud.databinding.FragmentTemplatesBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import com.example.gymbud.utility.populateWithSessions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class TemplatesFragment : Fragment() {
    private var _binding: FragmentTemplatesBinding? = null
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
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)

        binding.apply {
            libraryButton.setOnClickListener {
                val action = TemplatesFragmentDirections.actionTemplatesFragmentToProgramBuilderFragment()
                findNavController().navigate(action)
            }

            loadDefaultsButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.populateWithDefaults()

                    // todo remove this (generates test data)!!
                    val app = activity?.application as BaseApplication
                    populateWithSessions(app.programRepository, app.sessionRepository)
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hasData().collect { withData ->
                    if (withData) {
                        loadDefaultsButton.visibility = View.GONE
                    } else {
                        loadDefaultsButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }
}