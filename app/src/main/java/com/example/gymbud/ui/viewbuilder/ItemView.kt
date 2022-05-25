package com.example.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemContent
import com.example.gymbud.ui.viewmodel.ItemViewModel

interface ItemView {
    fun inflate(inflater: LayoutInflater): List<View>
    fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    )
}


interface EditItemView: ItemView {
    fun populateForNewItem(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel
    )

    fun getContent(): ItemContent?
}