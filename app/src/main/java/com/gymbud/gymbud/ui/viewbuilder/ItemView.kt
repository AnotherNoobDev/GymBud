package com.gymbud.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.ItemContent
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel

enum class Functionality {
    Detail,
    Edit
}

interface ItemView {
    fun inflate(inflater: LayoutInflater): List<View>
    fun populate(
        lifecycle: Lifecycle,
        lifecycleScope: LifecycleCoroutineScope,
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