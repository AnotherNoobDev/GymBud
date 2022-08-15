package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutEditDropdownFieldBinding
import com.example.gymbud.databinding.LayoutEditTextFieldBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel


private const val TAG = "ExerciseEV"


class ExerciseEditView(
    private val context: Context
): EditItemView {

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetMuscleBinding: LayoutEditDropdownFieldBinding? = null
    private val targetMuscleBinding get() = _targetMuscleBinding!!

    private var _notesBinding: LayoutEditTextFieldBinding? = null
    private val notesBinding get() = _notesBinding!!

    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)
        _targetMuscleBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        _notesBinding = LayoutEditTextFieldBinding.inflate(inflater)

        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        targetMuscleBinding.label.setStartIconDrawable(R.drawable.ic_target_muscle_24)

        notesBinding.label.setStartIconDrawable(R.drawable.ic_notes_24)
        notesBinding.label.hint = context.getString(R.string.item_notes)
        notesBinding.input.inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_FLAG_MULTI_LINE)

        targetMuscleBinding.label.hint = context.getString(R.string.target_muscle)

        val muscleGroups = MuscleGroup.values()
        val targetMuscleAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, muscleGroups)
        targetMuscleBinding.input.setAdapter(targetMuscleAdapter)


        return listOf(
            nameBinding.root,
            targetMuscleBinding.root,
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

        nameBinding.input.setText(item.name,  TextView.BufferType.SPANNABLE)
        targetMuscleBinding.input.setText(item.targetMuscle.toString(), false)
        notesBinding.input.setText(item.notes, TextView.BufferType.SPANNABLE)
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        targetMuscleBinding.input.setText(MuscleGroup.QUADS.toString(), false)
    }


    override fun getContent(): ItemContent? {
        if (!validateInput()) {
            return null
        }

        return ExerciseContent(
            nameBinding.input.text.toString(),
            notesBinding.input.text.toString(),
            MuscleGroup.valueOf(targetMuscleBinding.input.text.toString())
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