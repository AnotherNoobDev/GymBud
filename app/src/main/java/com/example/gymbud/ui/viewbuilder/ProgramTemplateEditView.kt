package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.*
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val TAG = "ProgramTemplateEV"

class ProgramTemplateEditView(
    private val context: Context
): EditItemView {

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _workoutListBinding: FragmentItemListBinding? = null
    private val workoutListBinding get() = _workoutListBinding!!

    private var addWorkoutTemplateButton = MaterialButton(context)
    private var addRestDayButton = MaterialButton(context)

    private var _addWorkoutBinding: FragmentItemEditBinding? = null
    private val addWorkoutBinding get() = _addWorkoutBinding!!

    private var _workoutSelectionBinding: LayoutEditDropdownFieldBinding? = null
    private val workoutSelectionBinding get() = _workoutSelectionBinding!!

    // the workouts(+rest days) in the set
    private var programTemplateEditableItems: MutableList<Item> = mutableListOf()
    private val workoutListAdapter = ProgramTemplateRecyclerViewAdapter(Functionality.Edit)

    // available workout templates to chose from
    private var workoutTemplates: List<Item>? = null
    private var workoutTemplatesSelectionAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.dropdown_list_item, listOf())

    // rest day
    private var restDay: Item = RestPeriod.RestDay

    init {
        workoutListAdapter.setOnItemClickedCallback { item ->
            if(programTemplateEditableItems.remove(item)) {
                workoutListAdapter.submitList(programTemplateEditableItems.toList())
            }
        }

        addWorkoutTemplateButton.setIconResource(R.drawable.ic_add_24)
        addWorkoutTemplateButton.text = context.getString(R.string.workout)
        addWorkoutTemplateButton.setOnClickListener{
            onAddNewWorkout()
        }

        addRestDayButton.setIconResource(R.drawable.ic_add_24)
        addRestDayButton.text = context.getString(R.string.rest_day)
        addRestDayButton.setOnClickListener{
            onAddNewRestDay()
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        return listOf(
            inflateName(inflater),
            inflateWorkoutList(inflater),
            LayoutDetailDividerBinding.inflate(inflater).root,
            inflateAddWorkout(inflater),
            addWorkoutTemplateButton,
            addRestDayButton,
        )
    }

    private fun inflateName(inflater: LayoutInflater): View {
        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)

        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        return nameBinding.root
    }

    private fun inflateWorkoutList(inflater: LayoutInflater): View {
        _workoutListBinding = FragmentItemListBinding.inflate(inflater)

        workoutListBinding.addItemFab.isVisible  = false
        workoutListBinding.recyclerView.adapter = workoutListAdapter

        return workoutListBinding.root
    }

    private fun inflateAddWorkout(inflater: LayoutInflater): View {
        _workoutSelectionBinding = LayoutEditDropdownFieldBinding.inflate(inflater)

        workoutSelectionBinding.input.setOnClickListener {
            workoutSelectionBinding.label.error = null
        }

        _addWorkoutBinding = FragmentItemEditBinding.inflate(inflater)

        addWorkoutBinding.apply {
            // move buttons closer
            val constraintSet = ConstraintSet()
            constraintSet.clone(layout)
            constraintSet.connect(buttonsLayout.id, ConstraintSet.TOP, editFieldsLayout.id, ConstraintSet.BOTTOM)
            constraintSet.applyTo(layout)

            editFieldsLayout.addView(workoutSelectionBinding.root)

            confirmBtn.text = context.getString(R.string.bnt_add)

            confirmBtn.setOnClickListener {
                setAddItemSectionVisibility(false)

                val name = workoutSelectionBinding.input.text.toString()
                if (name.isEmpty()) {
                    workoutSelectionBinding.label.error = "Name is required"
                    return@setOnClickListener
                }

                val item = workoutTemplates?.find { it.name == name } // todo name must be unique?!

                if (item == null) {
                    Log.e(TAG, "Failed to retrieve item to be added to set")
                    return@setOnClickListener
                }

                programTemplateEditableItems.add(item)
                workoutListAdapter.submitList(programTemplateEditableItems.toList())
            }

            cancelBtn.text = context.getString(R.string.btn_cancel)
            cancelBtn.setOnClickListener {
                setAddItemSectionVisibility(false)
            }
        }

        addWorkoutBinding.root.isVisible = false

        return addWorkoutBinding.root
    }


    private fun onAddNewWorkout() {
        setAddItemSectionVisibility(true)

        workoutSelectionBinding.label.setStartIconDrawable(R.drawable.ic_equipment_24)
        workoutSelectionBinding.label.hint = "Workout"

        workoutSelectionBinding.input.setAdapter(workoutTemplatesSelectionAdapter)
        workoutSelectionBinding.input.setText(workoutTemplates?.get(0)?.name ?: "", false) // todo all these ? look kinda funky xd
    }

    private fun onAddNewRestDay() {
        programTemplateEditableItems.add(restDay)
        workoutListAdapter.submitList(programTemplateEditableItems.toList())
    }


    private fun setAddItemSectionVisibility(visible: Boolean) {
        addWorkoutBinding.root.isVisible = visible
        addWorkoutTemplateButton.isVisible = !visible
        addRestDayButton.isVisible = !visible

    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        programTemplateEditableItems = mutableListOf()
        workoutListAdapter.submitList(programTemplateEditableItems.toList())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
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

        nameBinding.input.setText(item.name)

        programTemplateEditableItems = item.items.toMutableList()
        workoutListAdapter.submitList(programTemplateEditableItems.toList())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
    }

    private fun populateItemsThatCanBeAdded(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel
    ) {
        lifecycle.launch {
            viewModel.getItemsByType(ItemType.WORKOUT_TEMPLATE).collect {
                workoutTemplates = it

                val workoutTemplatesByName = it.map { workout ->
                    workout.name
                }

                workoutTemplatesSelectionAdapter =
                    ArrayAdapter(context, R.layout.dropdown_list_item, workoutTemplatesByName)
            }
        }
    }


    override fun getContent(): ItemContent? {
        if (!validateInput()) {
            return null
        }

        val name = nameBinding.input.text.toString()
        val setItems = programTemplateEditableItems.toList()

        return ProgramTemplateContent(
            name,
            setItems
        )
    }

    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        if (programTemplateEditableItems.size == 0) {
            nameBinding.label.error = "Program needs at least one item"
            return false
        }

        return true
    }
}