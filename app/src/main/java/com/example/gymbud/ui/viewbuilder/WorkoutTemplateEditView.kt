package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.*
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


private const val TAG = "WorkoutTemplateEV"

class WorkoutTemplateEditView(
    private val context: Context
): EditItemView {

    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _setTemplateListBinding: FragmentItemListBinding? = null
    private val setTemplateListBinding get() = _setTemplateListBinding!!

    private var addSetTemplateButton = MaterialButton(context)
    private var addRestPeriodButton = MaterialButton(context)

    private var _addItemBinding: FragmentItemEditBinding? = null
    private val addItemBinding get() = _addItemBinding!!

    private var _itemSelectionBinding: LayoutEditDropdownFieldBinding? = null
    private val itemSelectionBinding get() = _itemSelectionBinding!!

    private var _intensityBinding: LayoutEditDropdownFieldBinding? = null
    private val intensityBinding get() = _intensityBinding!!

    private var addingItemOfType = ItemType.SET_TEMPLATE

    // the set templates (+rest periods) in the workout
    private var workoutTemplateEditableItems: MutableList<Item> = mutableListOf()
    private val setListAdapter =  WorkoutTemplateRecyclerViewAdapter(context, Functionality.Edit)

    // available set templates to chose from
    private var setTemplates: List<Item>? = null
    private var setTemplatesSelectionAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.dropdown_list_item, listOf())

    // available rest periods to chose from
    private var restPeriods: List<Item>? = null
    private var restPeriodsSelectionAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.dropdown_list_item, listOf())

    init {
        setListAdapter.setOnItemClickedCallback { item ->
            if(workoutTemplateEditableItems.remove(item)) {
                setListAdapter.submitList(workoutTemplateEditableItems.toList())
            }
        }

        addSetTemplateButton.setIconResource(R.drawable.ic_add_24)
        addSetTemplateButton.text = context.getString(R.string.set)
        addSetTemplateButton.setOnClickListener{
            onAddNewSetTemplate()
        }

        addRestPeriodButton.setIconResource(R.drawable.ic_add_24)
        addRestPeriodButton.text = context.getString(R.string.rest_period)
        addRestPeriodButton.setOnClickListener{
            onAddNewRestPeriod()
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        return listOf(
            inflateName(inflater),
            inflateExerciseList(inflater),
            LayoutDetailDividerBinding.inflate(inflater).root,
            inflateAddItem(inflater),
            addSetTemplateButton,
            addRestPeriodButton,
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

    private fun inflateExerciseList(inflater: LayoutInflater): View {
        _setTemplateListBinding = FragmentItemListBinding.inflate(inflater)

        setTemplateListBinding.addItemFab.isVisible  = false
        setTemplateListBinding.recyclerView.adapter = setListAdapter

        return setTemplateListBinding.root
    }

    private fun inflateAddItem(inflater: LayoutInflater): View {
        _itemSelectionBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        _intensityBinding = LayoutEditDropdownFieldBinding.inflate(inflater)

        itemSelectionBinding.input.setOnClickListener {
            itemSelectionBinding.label.error = null
        }

        _addItemBinding = FragmentItemEditBinding.inflate(inflater)

        addItemBinding.apply {
            editFieldsLayout.addView(itemSelectionBinding.root)
            editFieldsLayout.addView(intensityBinding.root)

            confirmBtn.text = context.getString(R.string.bnt_add)

            confirmBtn.setOnClickListener {
                setAddItemSectionVisibility(false)

                val name = itemSelectionBinding.input.text.toString()
                if (name.isEmpty()) {
                    itemSelectionBinding.label.error = "Name is required"
                    return@setOnClickListener
                }

                var item = when (addingItemOfType) {
                    ItemType.SET_TEMPLATE -> setTemplates?.find { it.name == name }
                    ItemType.REST_PERIOD -> restPeriods?.find {it.name == name }
                    else -> null
                }

                if (item == null) {
                    Log.e(TAG, "Failed to retrieve item to be added to workout")
                    return@setOnClickListener
                }

                if (addingItemOfType == ItemType.SET_TEMPLATE) {
                    item = TaggedItem.makeTagged(item, TagCategory.Intensity, intensityBinding.input.text.toString())
                }


                workoutTemplateEditableItems.add(item)
                setListAdapter.submitList(workoutTemplateEditableItems.toList())
            }

            cancelBtn.text = context.getString(R.string.btn_cancel)
            cancelBtn.setOnClickListener {
                setAddItemSectionVisibility(false)
            }
        }

        addItemBinding.root.isVisible = false

        return addItemBinding.root
    }


    private fun onAddNewSetTemplate() {
        addingItemOfType = ItemType.SET_TEMPLATE

        setAddItemSectionVisibility(true)

        itemSelectionBinding.label.setStartIconDrawable(R.drawable.ic_equipment_24)
        itemSelectionBinding.label.hint = "Set"

        itemSelectionBinding.input.setAdapter(setTemplatesSelectionAdapter)
        itemSelectionBinding.input.setText(setTemplates?.get(0)?.name ?: "", false) // todo all these ? look kinda funky xd

        intensityBinding.label.setStartIconDrawable(R.drawable.ic_intensity_24)
        intensityBinding.label.hint = "Intensity"

        val intensityAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, SetIntensity.values().map { it.toString()})
        intensityBinding.input.setAdapter(intensityAdapter)
        intensityBinding.input.setText(SetIntensity.Working.toString(), false)
    }


    private fun onAddNewRestPeriod() {
        addingItemOfType = ItemType.REST_PERIOD

        setAddItemSectionVisibility(true)

        itemSelectionBinding.label.setStartIconDrawable(R.drawable.ic_timer_24)
        itemSelectionBinding.label.hint = "Rest"

        itemSelectionBinding.input.setAdapter(restPeriodsSelectionAdapter)
        itemSelectionBinding.input.setText(restPeriods?.get(0)?.name ?: "", false) // todo all these ? look kinda funky xd
    }

    private fun setAddItemSectionVisibility(visible: Boolean) {
        addItemBinding.root.isVisible = visible
        addSetTemplateButton.isVisible = !visible
        addRestPeriodButton.isVisible = !visible

        when (addingItemOfType) {
            ItemType.SET_TEMPLATE -> intensityBinding.root.visibility = View.VISIBLE
            else -> intensityBinding.root.visibility = View.GONE
        }
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        workoutTemplateEditableItems = mutableListOf()
        setListAdapter.submitList(workoutTemplateEditableItems.toList())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
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

        nameBinding.input.setText(item.name)

        workoutTemplateEditableItems = item.items.toMutableList()
        setListAdapter.submitList(workoutTemplateEditableItems.toList())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
    }

    private fun populateItemsThatCanBeAdded(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel
    ) {
        lifecycle.launch {
            viewModel.getItemsByType(ItemType.SET_TEMPLATE).collect {
                setTemplates = it

                val setTemplatesByName = it.map { set ->
                    set.name
                }

                setTemplatesSelectionAdapter =
                    ArrayAdapter(context, R.layout.dropdown_list_item, setTemplatesByName)
            }
        }

        lifecycle.launch {
            viewModel.getItemsByType(ItemType.REST_PERIOD).collect {
                restPeriods = it

                val restPeriodsByName = it.map { rest -> rest.name }

                restPeriodsSelectionAdapter =
                    ArrayAdapter(context, R.layout.dropdown_list_item, restPeriodsByName)
            }
        }
    }


    override fun getContent(): ItemContent? {
        if (!validateInput()) {
            return null
        }

        val name = nameBinding.input.text.toString()
        val workoutItems = workoutTemplateEditableItems.toList()

        return WorkoutTemplateContent(
            name,
            workoutItems
        )
    }

    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        if (workoutTemplateEditableItems.size == 0) {
            nameBinding.label.error = "Workout needs at least one item"
            return false
        }

        return true
    }
}