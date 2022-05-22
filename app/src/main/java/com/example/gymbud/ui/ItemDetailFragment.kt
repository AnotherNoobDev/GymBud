package com.example.gymbud.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentItemDetailBinding
import com.example.gymbud.ui.viewbuilder.ViewBuilder
import com.example.gymbud.ui.viewbuilder.ViewBuilderFactory
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory


private const val TAG = "ItemDetailFragment"


class ItemDetailFragment : Fragment() {

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private val viewModel: ItemViewModel by activityViewModels() {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private var _viewBuilder: ViewBuilder? = null
    private val viewBuilder get() = _viewBuilder!!

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        _viewBuilder = ViewBuilderFactory.create(navigationArgs.type)

        viewBuilder.inflate(inflater).forEach {
            binding.detailLayout.addView(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = viewModel.getItem(navigationArgs.id, navigationArgs.type)
        if (item == null) {
            Log.e(TAG, "Failed to retrieve item from viewmodel")
            return
        }

        viewBuilder.populate(item)

        binding.editFab.setOnClickListener {
            // todo make generic ItemAddFragment and connect it here
            /*
            val action = ExerciseDetailFragmentDirections.actionExerciseDetailFragmentToExerciseAddFragment()
            findNavController().navigate(action)

             */
        }
    }
}