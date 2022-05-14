package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentStartupBinding


class StartupFragment : Fragment() {
    private var _binding: FragmentStartupBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartupBinding.inflate(inflater, container, false)
        val view = binding.root

        val gotoDashboardButton = view.findViewById<Button>(R.id.to_dashboard)
        gotoDashboardButton.setOnClickListener {
            val action = StartupFragmentDirections.actionStartupFragmentToDashboardFragment()
            binding.root.findNavController().navigate(action)
        }

        val gotoNewSetupButton = view.findViewById<Button>(R.id.to_new_setup)
        gotoNewSetupButton.setOnClickListener {
            val action = StartupFragmentDirections.actionStartupFragmentToNewSetupFragment()
            binding.root.findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}