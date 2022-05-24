package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.gymbud.R
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.databinding.LayoutEditDropdownFieldBinding
import com.example.gymbud.databinding.LayoutEditRangeSliderBinding
import com.example.gymbud.databinding.LayoutEditTextFieldBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ExerciseTemplateEV"

class ExerciseTemplateEditView(
    private val context: Context
): EditItemView {

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseBinding: LayoutEditDropdownFieldBinding? = null
    private val exerciseBinding get() = _exerciseBinding!!

    private var _repRangeBinding: LayoutEditRangeSliderBinding? = null
    private val repRangeBinding get() = _repRangeBinding!!

    private var exercises: List<Item>? = null

    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)
        _exerciseBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        _repRangeBinding = LayoutEditRangeSliderBinding.inflate(inflater)

        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        exerciseBinding.label.setStartIconDrawable(R.drawable.ic_equipment_24)
        exerciseBinding.label.hint = context.getString(R.string.exercise)

        return listOf(
            nameBinding.root,
            exerciseBinding.root,
            repRangeBinding.root
        )
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is ExerciseTemplate) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        nameBinding.input.setText(item.name,  TextView.BufferType.SPANNABLE)
        exerciseBinding.input.setText(item.exercise.name, false)
        exerciseBinding.input.isEnabled = false
        repRangeBinding.input.values = mutableListOf<Float>(
            item.targetRepRange.first.toFloat(),
            item.targetRepRange.last.toFloat(),
        )
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        lifecycle.launch {
            viewModel.getItemsByType(ItemType.EXERCISE).collect {
                exercises = it
                val exercisesByName = it.map { ex ->
                    ex.name
                }

                val exerciseAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, exercisesByName)
                exerciseBinding.input.setAdapter(exerciseAdapter)
                exerciseBinding.input.setText(exercisesByName[0], false)
            }
        }
    }


    override fun getContent(): Item? {
        if (!validateInput()) {
            return null
        }

        // todo this doesn't feel safe (can 2 exercises have the same name? wouldn't that be confusing!?!)
        var exercise = exercises?.find { it.name == exerciseBinding.input.text.toString()}

        if (exercise == null)
        {
            // todo need to delegate this (build a placeholder exercise)
            // todo think we need 2 hierarchies here Item (this is an entity in the system) and ItemContent (this is a collection of values)
            exercise = Exercise(ItemIdentifierGenerator.generateTempId(), "", "",  MuscleGroup.BACK, ResistanceType.WEIGHT)
        }

        val name = nameBinding.input.text.toString()

        val targetRepRange = IntRange(
            repRangeBinding.input.values[0].toInt(),
            repRangeBinding.input.values[1].toInt(),
        )

        return ExerciseTemplate(
            ItemIdentifierGenerator.generateTempId(),
            name,
            exercise as Exercise,
            targetRepRange
        )
    }


    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        return true
    }
}