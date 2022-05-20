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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.data.ExerciseRepository
import com.example.gymbud.databinding.FragmentItemListBinding
import com.example.gymbud.model.ItemType
import com.example.gymbud.ui.viewmodel.ExerciseViewModel
import com.example.gymbud.ui.viewmodel.ExerciseViewModelFactory
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class ItemFragment : Fragment() {
    private val navigationArgs: ItemFragmentArgs by navArgs()

    private val viewModel: ItemViewModel by activityViewModels() {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
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
            /* todo can be determined if we know what we are displaying */
            val action = ItemFragmentDirections.actionAddItemFragmentToExerciseDetailFragment(it)
            findNavController().navigate(action)
        }

        /* todo needs to be given as input */
        viewLifecycleOwner.lifecycleScope.launch {
            // is this needed? https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            //repeatOnLifecycle(Lifecycle.State.STARTED) {

            viewModel.getItemsByTime(navigationArgs.itemType).collect{
                it.let {
                    adapter.submitList(it)
                }
            }
        }



        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)

            addItemFab.setOnClickListener{
                /* todo can be determined if we know what we are displaying */
                val action = ItemFragmentDirections.actionAddItemFragmentToExerciseAddFragment()
                findNavController().navigate(action)
            }
        }

    }
}