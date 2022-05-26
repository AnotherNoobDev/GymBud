package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.databinding.FragmentItemListBinding
import com.example.gymbud.databinding.LayoutDetailDividerBinding
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.ItemType
import com.example.gymbud.model.SetTemplate

import com.example.gymbud.ui.SetTemplateRecyclerViewAdapter
import com.example.gymbud.ui.viewmodel.ItemViewModel


private const val TAG = "SetTemplateDV"

class SetTemplateDetailView(
    private val onDetailsCallback: (ItemIdentifier, ItemType) -> Unit
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseListBinding: FragmentItemListBinding? = null
    private val exerciseListBinding get() = _exerciseListBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _exerciseListBinding = FragmentItemListBinding.inflate(inflater)

        exerciseListBinding.addItemFab.isVisible  = false;

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        return listOf(
            nameBinding.root,
            divider1,
            exerciseListBinding.root
        )
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is SetTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a set template!")
            return
        }

        nameBinding.name.text = item.name

        val adapter = SetTemplateRecyclerViewAdapter {
            onDetailsCallback(it, ItemType.EXERCISE_TEMPLATE)
        }

        adapter.submitList(item.items)

        exerciseListBinding.recyclerView.adapter = adapter
    }
}

