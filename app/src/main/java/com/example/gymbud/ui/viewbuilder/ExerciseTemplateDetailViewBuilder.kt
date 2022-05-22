package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.gymbud.databinding.LayoutDetailDividerBinding
import com.example.gymbud.databinding.LayoutDetailExerciseBinding
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.databinding.LayoutDetailRepRangeBinding
import com.example.gymbud.model.ExerciseTemplate
import com.example.gymbud.model.Item

private const val TAG = "ExerciseTemplateDVB"

class ExerciseTemplateDetailViewBuilder: ViewBuilder {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseBinding: LayoutDetailExerciseBinding? = null
    private val exerciseBinding get() = _exerciseBinding!!

    private var _targetRepRangeBinding: LayoutDetailRepRangeBinding? = null
    private val targetRepRangeBinding get() = _targetRepRangeBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _exerciseBinding = LayoutDetailExerciseBinding.inflate(inflater)
        _targetRepRangeBinding = LayoutDetailRepRangeBinding.inflate(inflater)

        val divider1 = LayoutDetailDividerBinding.inflate(inflater).root

        return listOf(
            nameBinding.root,
            exerciseBinding.root,
            divider1,
            targetRepRangeBinding.root
        )
    }

    override fun populate(item: Item) {
        if (item !is ExerciseTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise template!")
            return
        }

        nameBinding.name.text = item.name

        exerciseBinding.exercise.text = item.exercise.name
        targetRepRangeBinding.repRange.text = item.targetRepRange.toString() + " reps"
    }
}