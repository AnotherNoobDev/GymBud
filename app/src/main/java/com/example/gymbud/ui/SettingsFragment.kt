package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.gymbud.BaseApplication
import com.example.gymbud.data.AppRepository
import com.example.gymbud.databinding.FragmentSettingsBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private lateinit var appRepository: AppRepository


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.apply {
            resetDbButton.setOnClickListener {
                // todo ask user to confirm action and authenticate (and inform of consequences)

                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        viewModel.removeAll()

                        // todo provide confirmation message after completion (snackbar: https://material.io/components/snackbars)
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    appRepository.reset()
                }
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository
    }
}