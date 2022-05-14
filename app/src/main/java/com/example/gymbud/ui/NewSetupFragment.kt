package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentNewSetupBinding


class NewSetupFragment : Fragment() {
    private var _binding: FragmentNewSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewSetupBinding.inflate(inflater, container, false)
        val view = binding.root

        val createProgramButton = view.findViewById<Button>(R.id.create_program_button)
        createProgramButton.setOnClickListener {
            val action = NewSetupFragmentDirections.actionNewSetupFragmentToProgramBuilderFragment()
            binding.root.findNavController().navigate(action)
        }

        return view
    }
}