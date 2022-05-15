package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.gymbud.R
import com.example.gymbud.data.ExerciseRepository
import com.example.gymbud.databinding.FragmentItemListBinding
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import com.example.gymbud.ui.viewmodel.ExerciseViewModelFactory

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {
    /* todo: pass the list of items in to the fragment */
    private val viewModel: ExerciseViewModel by activityViewModels() {
        ExerciseViewModelFactory()
    }

    private var _binding: FragmentItemListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ItemRecyclerViewAdapter{
            val action = ItemFragmentDirections.actionAddItemFragmentToExerciseDetailFragment(it)
            findNavController().navigate(action)
        }

        viewModel.exercises.observe(this.viewLifecycleOwner) {
            it.let {
                adapter.submitList(it)
            }
        }

        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            addItemFab.setOnClickListener{
                val action = ItemFragmentDirections.actionAddItemFragmentToExerciseAddFragment()
                findNavController().navigate(action)
            }
        }

    }
}