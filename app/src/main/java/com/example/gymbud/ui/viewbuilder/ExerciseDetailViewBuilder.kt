package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.gymbud.databinding.*
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.Item

private const val TAG = "ExerciseDVB"


class ExerciseDetailViewBuilder: ViewBuilder {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutDetailTargetMuscleBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _equipmentBinding: LayoutDetailEquipmentBinding? = null
    private val equipmentBinding get() = _equipmentBinding!!

    private var _notesBinding: LayoutDetailNotesBinding? = null
    private val notesBinding get() = _notesBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _targetMuscleBinding = LayoutDetailTargetMuscleBinding.inflate(inflater)
        _equipmentBinding = LayoutDetailEquipmentBinding.inflate(inflater)
        _notesBinding = LayoutDetailNotesBinding.inflate(inflater)

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

    override fun populate(item: Item) {
        if (item !is Exercise) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        nameBinding.name.text = item.name
        targetMuscleBinding.targetMuscle.text = item.targetMuscle.toString()

        equipmentBinding.equipment.text = item.resistance.toString()
        notesBinding.notes.text = item.description
    }
}