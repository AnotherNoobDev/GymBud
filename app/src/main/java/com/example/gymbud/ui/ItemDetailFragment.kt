package com.example.gymbud.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.databinding.FragmentItemDetailBinding
import com.example.gymbud.ui.viewbuilder.ItemView
import com.example.gymbud.ui.viewbuilder.ItemViewFactory
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch



class ItemDetailFragment : Fragment() {

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private var _itemView: ItemView? = null
    private val itemView get() = _itemView!!

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        _itemView = ItemViewFactory.create(navigationArgs.type, requireContext()) { itemId, itemType ->
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentSelf(itemId, itemType)
            findNavController().navigate(action)
        }

        itemView.inflate(inflater).forEach {
            binding.detailLayout.addView(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getItem(navigationArgs.id, navigationArgs.type).collect { item ->
                if (item != null) {
                    itemView.populate(viewLifecycleOwner.lifecycleScope, viewModel, item)
                }
            }
        }

        binding.editFab.setOnClickListener {
            val action = ItemDetailFragmentDirections.actionItemDetailFragmentToItemEditFragment(navigationArgs.id, navigationArgs.type)
            findNavController().navigate(action)
        }
    }
}