package com.gymbud.gymbud.ui.guides

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gymbud.gymbud.databinding.FragmentCreatingProgramGuideBinding


class CreatingProgramGuideFragment : Fragment() {
    private var _binding: FragmentCreatingProgramGuideBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatingProgramGuideBinding.inflate(inflater, container, false)
        return binding.root
    }
}