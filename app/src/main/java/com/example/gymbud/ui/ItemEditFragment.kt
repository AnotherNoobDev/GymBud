package com.example.gymbud.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.gymbud.BaseApplication
import com.example.gymbud.R
import com.example.gymbud.databinding.FragmentItemEditBinding
import com.example.gymbud.ui.viewbuilder.EditItemView
import com.example.gymbud.ui.viewbuilder.EditItemViewFactory
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.example.gymbud.ui.viewmodel.ItemViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ItemEditFragment : Fragment() {

    private val navigationArgs: ItemEditFragmentArgs by navArgs()

    private val viewModel: ItemViewModel by activityViewModels() {
        ItemViewModelFactory(
            (activity?.application as BaseApplication).itemRepository
        )
    }

    private var _itemView: EditItemView? = null
    private val itemView get() = _itemView!!

    private var _binding: FragmentItemEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemEditBinding.inflate(inflater, container, false)

        binding.apply {
            confirmBtn.text = requireContext().getString(R.string.save)
            cancelBtn.text = requireContext().getString(R.string.delete)
        }

        _itemView = EditItemViewFactory.create(navigationArgs.type, requireContext())

        itemView.inflate(inflater).forEach {
            binding.editFieldsLayout.addView(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.id

        if (id >= 0) {
            onViewCreatedWithExistingItem()
        } else {
            onViewCreatedWithNewItem()
        }
    }


    private fun onViewCreatedWithExistingItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getItem(navigationArgs.id, navigationArgs.type).collect { item ->
                if (item != null) {
                    itemView.populate(viewLifecycleOwner.lifecycleScope, viewModel, item)
                }
            }
        }


        binding.apply {
            confirmBtn.setOnClickListener {
                updateItem()
            }

            cancelBtn.visibility = View.VISIBLE
            cancelBtn.setOnClickListener {
                removeItem()
            }
        }
    }


    private fun onViewCreatedWithNewItem() {
        itemView.populateForNewItem(viewLifecycleOwner.lifecycleScope, viewModel)

        binding.apply {
            confirmBtn.setOnClickListener {
                addItem()
            }

            cancelBtn.visibility = View.GONE
        }
    }


    private fun addItem() {
        val tempItem = itemView.getContent() ?: return

        // todo this way or  viewModelScope inside ItemViewModel?
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addItem(tempItem)
            val action = ItemEditFragmentDirections.actionItemEditFragmentToItemListFragment(navigationArgs.type)
            findNavController().navigate(action)
        }
    }


    // todo seems kinda duplicate with addExercise
    private fun updateItem() {
        val tempItem = itemView.getContent() ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateItem(navigationArgs.id, tempItem)
            val action = ItemEditFragmentDirections.actionItemEditFragmentToItemListFragment(navigationArgs.type)
            findNavController().navigate(action)
        }
    }


    private fun removeItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.removeItem(navigationArgs.id, navigationArgs.type)
            val action = ItemEditFragmentDirections.actionItemEditFragmentToItemListFragment(navigationArgs.type)
            findNavController().navigate(action)
        }
    }
}