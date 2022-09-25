package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.databinding.FragmentBasicItemListBinding
import com.example.gymbud.databinding.LayoutDetailDividerBinding
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.model.*

import com.example.gymbud.ui.viewmodel.ItemViewModel


private const val TAG = "TemplateWithItemsDV"


class TemplateWithItemsDetailView(
    val context: Context,
    private val onDetailsCallback: (Item) -> Unit
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _itemListBinding: FragmentBasicItemListBinding? = null
    private val itemListBinding get() = _itemListBinding!!

    private val itemListAdapter =  TemplateWithItemsRecyclerViewAdapter(context, Functionality.Detail)

    init {
        itemListAdapter.setOnItemClickedCallback { item, _ ->
            onDetailsCallback(item)
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        _itemListBinding = FragmentBasicItemListBinding.inflate(inflater)
        itemListBinding.root.setPadding(0,0,0,0)
        itemListBinding.recyclerView.adapter = itemListAdapter

        return listOf(
            nameBinding.root,
            divider1,
            itemListBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is ItemContainer) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a Template with Items (ItemContainer)!")
            return
        }

        nameBinding.name.text = item.name

        itemListAdapter.submitList(item.items)
    }
}

