package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.*
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.Item
import com.example.gymbud.ui.viewmodel.ItemViewModel

private const val TAG = "ExerciseDV"


class ExerciseDetailView: ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutDetailTextFieldBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _equipmentBinding: LayoutDetailTextFieldBinding? = null
    private val equipmentBinding get() = _equipmentBinding!!

    private var _notesBinding: LayoutDetailTextFieldBinding? = null
    private val notesBinding get() = _notesBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _targetMuscleBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        _equipmentBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        _notesBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        targetMuscleBinding.icon.setImageResource(R.drawable.ic_target_muscle_24)
        equipmentBinding.icon.setImageResource(R.drawable.ic_equipment_24)
        notesBinding.icon.setImageResource(R.drawable.ic_notes_24)
        notesBinding.text.isSingleLine = false

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root
        val divider2 = LayoutDetailDividerBinding.inflate(inflater).root


        return listOf(
            nameBinding.root,
            targetMuscleBinding.root,
            divider1,
            equipmentBinding.root,
            divider2,
            notesBinding.root
        )
    }

    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is Exercise) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        nameBinding.name.text = item.name
        targetMuscleBinding.text.text = item.targetMuscle.toString()
        equipmentBinding.text.text = item.resistance.toString()
        notesBinding.text.text = item.notes
    }
}