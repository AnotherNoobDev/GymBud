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

private const val TAG = "ProgramTemplateDV"

// todo lots of duplication with SetTemplateDetailView atm (basically copy-pasta, only big diff is adapter (setListAdapter))
//  -> can we do better? will other things change in the "final" version to justify keeping them separate? (should still try to remove duplication)
class ProgramTemplateDetailView(
    private val onDetailsCallback: (ItemIdentifier, ItemType) -> Unit
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _workoutListBinding: FragmentItemListBinding? = null
    private val workoutListBinding get() = _workoutListBinding!!

    private val workoutListAdapter = ProgramTemplateRecyclerViewAdapter(Functionality.Detail)

    init {
        workoutListAdapter.setOnItemClickedCallback {
            if (it is WorkoutTemplate) {
                onDetailsCallback(it.id, ItemType.WORKOUT_TEMPLATE)
            }
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _workoutListBinding = FragmentItemListBinding.inflate(inflater)

        workoutListBinding.addItemFab.isVisible  = false
        workoutListBinding.recyclerView.adapter = workoutListAdapter

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        return listOf(
            nameBinding.root,
            divider1,
            workoutListBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is ProgramTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a program template!")
            return
        }

        nameBinding.name.text = item.name

        workoutListAdapter.submitList(item.items)
    }
}