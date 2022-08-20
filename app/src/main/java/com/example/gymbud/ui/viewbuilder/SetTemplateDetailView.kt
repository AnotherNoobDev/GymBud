package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.databinding.FragmentItemListBinding
import com.example.gymbud.databinding.LayoutDetailDividerBinding
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.model.*

import com.example.gymbud.ui.viewmodel.ItemViewModel


private const val TAG = "SetTemplateDV"

class SetTemplateDetailView(
    private val onDetailsCallback: (ItemIdentifier, ItemType) -> Unit
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseListBinding: FragmentItemListBinding? = null
    private val exerciseListBinding get() = _exerciseListBinding!!

    private val exerciseListAdapter = SetTemplateRecyclerViewAdapter(Functionality.Detail)

    init {
        exerciseListAdapter.setOnItemClickedCallback {
            if (it is ExerciseTemplate) {
                onDetailsCallback(it.id, ItemType.EXERCISE_TEMPLATE)
            }
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _exerciseListBinding = FragmentItemListBinding.inflate(inflater)

        exerciseListBinding.addItemFab.isVisible  = false
        exerciseListBinding.recyclerView.adapter = exerciseListAdapter

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        return listOf(
            nameBinding.root,
            divider1,
            exerciseListBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
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

        exerciseListAdapter.submitList(item.items)
    }
}

