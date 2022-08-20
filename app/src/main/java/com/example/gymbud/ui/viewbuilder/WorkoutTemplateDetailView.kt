package com.example.gymbud.ui.viewbuilder

import android.content.Context
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


private const val TAG = "WorkoutTemplateDV"

// todo lots of duplication with SetTemplateDetailView atm (basically copy-pasta, only big diff is adapter (setListAdapter))
//  -> can we do better? will other things change in the "final" version to justify keeping them separate? (should still try to remove duplication)
class WorkoutTemplateDetailView(
    val context: Context,
    private val onDetailsCallback: (ItemIdentifier, ItemType) -> Unit
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _setListBinding: FragmentItemListBinding? = null
    private val setListBinding get() = _setListBinding!!

    private val setListAdapter = WorkoutTemplateRecyclerViewAdapter(context, Functionality.Detail)

    init {
        setListAdapter.setOnItemClickedCallback {
            if (it is SetTemplate) {
                onDetailsCallback(it.id, ItemType.SET_TEMPLATE)
            } else if (it is TaggedItem && it.item is SetTemplate) {
                onDetailsCallback(it.id, ItemType.SET_TEMPLATE)
            }
        }
    }

    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _setListBinding = FragmentItemListBinding.inflate(inflater)

        setListBinding.addItemFab.isVisible  = false
        setListBinding.recyclerView.adapter = setListAdapter

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        return listOf(
            nameBinding.root,
            divider1,
            setListBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is WorkoutTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a workout template!")
            return
        }

        nameBinding.name.text = item.name

        setListAdapter.submitList(item.items)
    }
}

