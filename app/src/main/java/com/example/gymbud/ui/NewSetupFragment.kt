package com.example.gymbud.ui

import android.opengl.Visibility
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
import com.example.gymbud.databinding.FragmentNewSetupBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class NewSetupFragment : Fragment() {
    private var _binding: FragmentNewSetupBinding? = null
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
        _binding = FragmentNewSetupBinding.inflate(inflater, container, false)

        binding.apply {
            createButton.setOnClickListener {
                val action = NewSetupFragmentDirections.actionNewSetupFragmentToProgramBuilderFragment()
                findNavController().navigate(action)
            }

            importButton.setOnClickListener {
                // todo add Import functionality
            }

            useDefaultsButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.populateWithDefaults()
                }
            }

            browseTemplatesButton.setOnClickListener {
                val action = NewSetupFragmentDirections.actionNewSetupFragmentToProgramBuilderFragment()
                findNavController().navigate(action)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.hasData().collect { withData ->
                    if (withData) {
                        browseTemplatesButton.visibility = View.VISIBLE
                        useDefaultsButton.visibility = View.GONE
                    } else {
                        browseTemplatesButton.visibility = View.GONE
                        useDefaultsButton.visibility = View.VISIBLE
                    }
                }
            }
        }

        return binding.root
    }
}