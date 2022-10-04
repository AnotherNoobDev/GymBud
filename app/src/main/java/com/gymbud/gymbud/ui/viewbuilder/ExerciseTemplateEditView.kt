package com.gymbud.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.gymbud.gymbud.R
import com.gymbud.gymbud.databinding.LayoutDetailNameBinding
import com.gymbud.gymbud.databinding.LayoutEditDropdownFieldBinding
import com.gymbud.gymbud.databinding.LayoutEditRangeSliderBinding
import com.gymbud.gymbud.databinding.LayoutEditTextFieldBinding
import com.gymbud.gymbud.model.*
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ExerciseTemplateEV"

class ExerciseTemplateEditView(
    private val context: Context
): EditItemView {

    private var _titleBinding: LayoutDetailNameBinding? = null
    private val titleBinding get() = _titleBinding!!

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _exerciseBinding: LayoutEditDropdownFieldBinding? = null
    private val exerciseBinding get() = _exerciseBinding!!

    private var _repRangeBinding: LayoutEditRangeSliderBinding? = null
    private val repRangeBinding get() = _repRangeBinding!!

    private var exercises: List<Item>? = null

    override fun inflate(inflater: LayoutInflater): List<View> {
        _titleBinding = LayoutDetailNameBinding.inflate(inflater)

        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)
        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        _exerciseBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        exerciseBinding.label.setStartIconDrawable(R.drawable.ic_equipment_24)
        exerciseBinding.label.hint = context.getString(R.string.exercise)

        _repRangeBinding = LayoutEditRangeSliderBinding.inflate(inflater)

        return listOf(
            titleBinding.root,
            nameBinding.root,
            exerciseBinding.root,
            repRangeBinding.root
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
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not an exercise!")
            return
        }

        titleBinding.name.text="Modify Exercise Template"
        nameBinding.input.setText(item.name,  TextView.BufferType.SPANNABLE)
        exerciseBinding.input.setText(item.exercise.name, false)
        exerciseBinding.input.isEnabled = false
        repRangeBinding.input.values = mutableListOf(
            item.targetRepRange.first.toFloat(),
            item.targetRepRange.last.toFloat(),
        )
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        titleBinding.name.text="Add Exercise Template"

        lifecycle.launch {
            viewModel.getItemsByType(ItemType.EXERCISE).collect {
                exercises = it
                val exercisesByName = it.map { ex ->
                    ex.name
                }

                if (exercisesByName.isEmpty()) {
                    exerciseBinding.label.error = "No Exercises available"
                } else {
                    exerciseBinding.label.error = null

                    val exerciseAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, exercisesByName)
                    exerciseBinding.input.setAdapter(exerciseAdapter)
                    exerciseBinding.input.setText(exercisesByName[0], false)
                }
            }
        }
    }


    override fun getContent(): ItemContent? {
        if (!validateInput()) {
            return null
        }

        val exercise = exercises?.find { it.name == exerciseBinding.input.text.toString()}

        val name = nameBinding.input.text.toString()

        val targetRepRange = IntRange(
            repRangeBinding.input.values[0].toInt(),
            repRangeBinding.input.values[1].toInt(),
        )

        // the Exercise cannot be changed when we modify an ExerciseTemplate
        // so the exercise input field will convey a null Exercise
        return if (exercise == null) {
            ExerciseTemplateEditContent(name, targetRepRange)
        } else {
            ExerciseTemplateNewContent(
                name,
                exercise as Exercise,
                targetRepRange
            )
        }
    }


    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        // input is not valid if we are supposed to add an Exercise, but exercises has none
        if (exercises != null && exercises!!.isEmpty()) {
            return false
        }

        return true
    }
}