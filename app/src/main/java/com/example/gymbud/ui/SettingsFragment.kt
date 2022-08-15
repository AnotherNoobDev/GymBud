package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.data.repository.AppRepository
import com.example.gymbud.databinding.FragmentSettingsBinding
import com.example.gymbud.model.WeightUnit
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
            // Disable dev options in production
            // todo How do distinguish between dev and production environments?
            val isProduction = true
            if (isProduction) {
                devOptions.visibility = View.GONE
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = (activity?.application as BaseApplication).appRepository

        binding.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                appRepository.weightUnit.collect {
                    when (it) {
                        WeightUnit.KG -> weightUnitGroup.check(R.id.weight_unit_kg)
                        WeightUnit.LB -> weightUnitGroup.check(R.id.weight_unit_lb)
                    }
                }
            }

            weightUnitKg.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        appRepository.updateWeightUnit(WeightUnit.KG)
                    }
                }
            }

            weightUnitLb.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        appRepository.updateWeightUnit(WeightUnit.LB)
                    }
                }
            }

            resetDbButton.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        viewModel.removeAll()
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    appRepository.reset()
                }
            }
        }
    }
}