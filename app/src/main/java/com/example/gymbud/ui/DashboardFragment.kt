package com.example.gymbud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentDashboardBinding
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory


class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
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
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.apply {
            activeProgramEditBtn.setOnClickListener {
                // todo change active program
                //  what kind of consequences does this have? make user confirm?
            }

            activeWorkoutEditBtn.setOnClickListener {
                // todo change today's workout
                //  what kind of consequences does this have? make user confirm?
            }


        }

        return binding.root
    }
}