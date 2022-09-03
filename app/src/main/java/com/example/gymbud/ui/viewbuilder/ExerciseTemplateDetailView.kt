package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailDividerBinding
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel

private const val TAG = "ExerciseTemplateDVB"

class ExerciseTemplateDetailView(
    private val onDetailsCallback: ((Item) -> Unit)? = null
): ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseBinding: LayoutDetailTextFieldBinding? = null
    private val exerciseBinding get() = _exerciseBinding!!

    private var _targetRepRangeBinding: LayoutDetailTextFieldBinding? = null
    private val targetRepRangeBinding get() = _targetRepRangeBinding!!

    private var exerciseTemplate: ExerciseTemplate? = null

    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)

        _exerciseBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        exerciseBinding.icon.setImageResource(R.drawable.ic_equipment_24)
        exerciseBinding.iconNavigateTo.visibility = View.VISIBLE

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        _targetRepRangeBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        targetRepRangeBinding.icon.setImageResource(R.drawable.ic_range_24)

        return listOf(
            nameBinding.root,
            exerciseBinding.root,
            divider1,
            targetRepRangeBinding.root
        )
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is ExerciseTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise template!")
            return
        }

        populate(item)
    }


    fun populate(item: ExerciseTemplate) {
        exerciseTemplate = item

        nameBinding.name.text = item.name

        exerciseBinding.text.text = item.exercise.name
        exerciseBinding.container.setOnClickListener {
            onDetailsCallback?.let { it1 -> it1(exerciseTemplate!!.exercise) }
        }

        targetRepRangeBinding.text.text = item.targetRepRange.toString() + " reps"
    }
}