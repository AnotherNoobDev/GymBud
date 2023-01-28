package com.gymbud.gymbud.ui.viewbuilder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gymbud.gymbud.R
import com.gymbud.gymbud.databinding.*
import com.gymbud.gymbud.model.*
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.min


private const val TAG = "TemplateWithItemsEV"

class TemplateWithItemsEditView(
    private val context: Context,
    private val templateType: ItemType,
    private val onFullscreenCallback: (Boolean) -> Unit
): EditItemView {
    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _itemListBinding: FragmentBasicItemListBinding? = null
    private val itemListBinding get() = _itemListBinding!!

    private val addButtonsLayout = LinearLayout(context)

    private val addTemplateItemButton = MaterialButton(context)
    private val addRestPeriodButton = MaterialButton(context)

    private var _endDelimiterBinding: LayoutDetailDividerBinding? = null
    private val endDelimiterBinding get() = _endDelimiterBinding!!

    private var _addItemBinding: FragmentItemEditBinding? = null
    private val addItemBinding get() = _addItemBinding!!

    private var addingItemOfType = ItemType.UNKNOWN

    private var _itemSelectionBinding: LayoutEditDropdownFieldBinding? = null
    private val itemSelectionBinding get() = _itemSelectionBinding!!

    private var _intensityBinding: LayoutEditDropdownFieldBinding? = null
    private val intensityBinding get() = _intensityBinding!!

    private val itemListAdapter = TemplateWithItemsRecyclerViewAdapter(context, Functionality.Edit)
    private val itemListDragDrop by lazy { ItemTouchHelper(RecyclerViewDragDrop()) }

    // available exercise templates to chose from
    private var availableTemplates: List<Item>? = null
    private var availableTemplatesSelectionAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.dropdown_list_item, listOf())

    // available rest periods to chose from
    private var restPeriods: List<Item>? = null
    private var restPeriodsSelectionAdapter: ArrayAdapter<String> = ArrayAdapter(context, R.layout.dropdown_list_item, listOf())


    init {
        assert(
            templateType == ItemType.SET_TEMPLATE ||
            templateType == ItemType.WORKOUT_TEMPLATE ||
            templateType == ItemType.PROGRAM_TEMPLATE
        )

        addTemplateItemButton.setIconResource(R.drawable.ic_add_24)
        addTemplateItemButton.text = templateTypeToDisplayStr(getTemplateItemType())
        addTemplateItemButton.setOnClickListener {
            onAddNewTemplateItem(getTemplateItemType())
        }

        addRestPeriodButton.setIconResource(R.drawable.ic_add_24)
        addRestPeriodButton.text = if (templateType == ItemType.PROGRAM_TEMPLATE) "Rest Day" else "Rest"
        addRestPeriodButton.setOnClickListener{
            onAddNewRestPeriod()
        }

        addButtonsLayout.addView(addTemplateItemButton)
        addButtonsLayout.addView(addRestPeriodButton)
        addButtonsLayout.orientation = LinearLayout.HORIZONTAL
    }


    // the type of work items the template contains
    private fun getTemplateItemType(): ItemType {
        return when (templateType) {
            ItemType.SET_TEMPLATE -> ItemType.EXERCISE_TEMPLATE
            ItemType.WORKOUT_TEMPLATE -> ItemType.SET_TEMPLATE
            ItemType.PROGRAM_TEMPLATE -> ItemType.WORKOUT_TEMPLATE
            else -> ItemType.UNKNOWN
        }
    }


    override fun inflate(inflater: LayoutInflater): List<View> {
        _endDelimiterBinding = LayoutDetailDividerBinding.inflate(inflater)

        val views = listOf(
            inflateName(inflater),
            inflateItemList(inflater),
            LayoutDetailDividerBinding.inflate(inflater).root,
            inflateAddItem(inflater),
            addButtonsLayout.rootView,
            endDelimiterBinding.root
        )

        setupLayoutParams(views)

        return views
    }


    private fun inflateName(inflater: LayoutInflater): View {
        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)

        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }

        return nameBinding.root
    }


    private fun inflateItemList(inflater: LayoutInflater): View {
        _itemListBinding = FragmentBasicItemListBinding.inflate(inflater)

        itemListBinding.root.setPadding(0,0,0,0)
        itemListBinding.recyclerView.adapter = itemListAdapter

        itemListDragDrop.attachToRecyclerView(itemListBinding.recyclerView)

        return itemListBinding.root
    }


    private fun inflateAddItem(inflater: LayoutInflater): View {
        _itemSelectionBinding = LayoutEditDropdownFieldBinding.inflate(inflater)
        _intensityBinding = LayoutEditDropdownFieldBinding.inflate(inflater)

        _addItemBinding = FragmentItemEditBinding.inflate(inflater)

        addItemBinding.apply {
            layout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

            editFieldsLayout.addView(itemSelectionBinding.root)
            editFieldsLayout.addView(intensityBinding.root)

            editFieldsLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f)

            val newButtonsLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f)
            newButtonsLayoutParams.setMargins(0, 0, 0, 0)
            buttonsLayout.layoutParams = newButtonsLayoutParams

            detailsBtn.text = context.getString(R.string.details)

            confirmBtn.text = context.getString(R.string.bnt_add)

            confirmBtn.setOnClickListener {
                nameBinding.label.error = null

                val name = itemSelectionBinding.input.text.toString()
                if (name.isEmpty()) {
                    return@setOnClickListener
                }

                var item = when (addingItemOfType) {
                    ItemType.EXERCISE_TEMPLATE, ItemType.SET_TEMPLATE, ItemType.WORKOUT_TEMPLATE -> availableTemplates?.find { it.name == name }
                    ItemType.REST_PERIOD -> restPeriods?.find {it.name == name }
                    else -> null
                }

                if (item == null) {
                    //Log.e(TAG, "Failed to retrieve item to be added to set")
                    return@setOnClickListener
                }

                if (addingItemOfType == ItemType.SET_TEMPLATE) {
                    item = TaggedItem.makeTagged(item, TagCategory.Intensity, intensityBinding.input.text.toString())
                }

                setAddItemSectionVisibility(false)

                updateItemListAdapter(item)
            }

            cancelBtn.text = context.getString(R.string.btn_cancel)
            cancelBtn.setOnClickListener {
                setAddItemSectionVisibility(false)
            }

            // smaller margin between buttons so that they all fit
            val individualButtonLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            individualButtonLayoutParams.setMargins(0, 0, 8, 0)

            cancelBtn.layoutParams = individualButtonLayoutParams
            detailsBtn.layoutParams = individualButtonLayoutParams
        }

        addItemBinding.root.isVisible = false

        return addItemBinding.root
    }


    private fun updateItemListAdapter(item: Item) {
        val newList = itemListAdapter.currentList.toMutableList()

        var insertAt = itemListAdapter.getCurrentSelectionPos()
        if (insertAt == RecyclerView.NO_POSITION) {
            insertAt = itemListAdapter.itemCount - 1
        }

        insertAt += 1

        newList.add(insertAt, item)

        itemListAdapter.submitList(newList, insertAt)

        // make sure last item added is in view
        itemListBinding.recyclerView.scrollToPosition(min(insertAt + 2, itemListAdapter.itemCount - 1))
    }


    private fun onAddNewTemplateItem(type: ItemType) {
        addingItemOfType = type

        presentInfoButton(addingItemOfType)

        setAddItemSectionVisibility(true)

        itemSelectionBinding.label.setStartIconDrawable(R.drawable.ic_equipment_24)
        itemSelectionBinding.label.hint = templateTypeToDisplayStr(type)

        if (availableTemplates == null || availableTemplates!!.isEmpty()) {
            itemSelectionBinding.label.error = "No templates available"
        } else {
            itemSelectionBinding.label.error = null
            itemSelectionBinding.input.setAdapter(availableTemplatesSelectionAdapter)
            itemSelectionBinding.input.setText(availableTemplates!![0].name, false)
        }


        intensityBinding.label.setStartIconDrawable(R.drawable.ic_intensity_24)
        intensityBinding.label.hint = "Intensity"

        val intensityAdapter = ArrayAdapter(context, R.layout.dropdown_list_item, SetIntensity.values().map { it.toString()})
        intensityBinding.input.setAdapter(intensityAdapter)
        intensityBinding.input.setText(SetIntensity.Working.toString(), false)
    }


    private fun presentInfoButton(type: ItemType) {
        when(type) {
            ItemType.SET_TEMPLATE, ItemType.WORKOUT_TEMPLATE ->  {
                addItemBinding.detailsBtn.apply {
                    visibility = View.VISIBLE
                    isEnabled = true
                    setOnClickListener {
                        openDetailsDialog()
                    }
                }
            }
            else -> {
                addItemBinding.detailsBtn.apply {
                    visibility = View.VISIBLE
                    isEnabled = false
                    setOnClickListener {
                        // no-op
                    }
                }
            }
        }
    }


    private fun openDetailsDialog() {
        val name = itemSelectionBinding.input.text.toString()
        if (name.isEmpty()) {
            return
        }

        val item = availableTemplates?.find { it.name == name } ?: return

        if (item !is ItemContainer) {
            return
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(name)
            .setMessage("\n" + item.items.joinToString("\n\n") { it.name })
            .setPositiveButton("Dismiss") {_,_ ->
            }
            .show()
    }


    private fun onAddNewRestPeriod() {
        if (templateType == ItemType.PROGRAM_TEMPLATE) {
            updateItemListAdapter(RestPeriod.RestDay)
            return
        }

        addingItemOfType = ItemType.REST_PERIOD

        presentInfoButton(addingItemOfType)

        setAddItemSectionVisibility(true)

        itemSelectionBinding.label.setStartIconDrawable(R.drawable.ic_timer_24)
        itemSelectionBinding.label.hint = "Rest"

        if (restPeriods == null || restPeriods!!.isEmpty()) {
            itemSelectionBinding.label.error = "No Rest Periods available"
        } else {
            itemSelectionBinding.label.error = null
            itemSelectionBinding.input.setAdapter(restPeriodsSelectionAdapter)
            itemSelectionBinding.input.setText(restPeriods!![0].name, false)
        }
    }


    private fun setAddItemSectionVisibility(visible: Boolean) {
        addItemBinding.root.visibility = if (visible) View.VISIBLE else View.GONE
        addTemplateItemButton.visibility = if (visible) View.GONE else View.VISIBLE
        addRestPeriodButton.visibility = if (visible) View.GONE else View.VISIBLE
        endDelimiterBinding.root.visibility = if (visible) View.GONE else View.VISIBLE

        if (addingItemOfType != ItemType.SET_TEMPLATE) {
            intensityBinding.root.visibility = View.GONE
        } else {
            intensityBinding.root.visibility = View.VISIBLE
        }

        // take up as much screen space as possible when adding item
        onFullscreenCallback(visible)
    }


    private fun setupLayoutParams(views: List<View>) {
        // TODO this also overwrites margins, etc .. doesn't matter now but might cause bugs later.. can we do better?
        views.forEach {
            val params = it.layoutParams
            if (params == null) {
                it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.0f)
            } else {
                it.layoutParams = LinearLayout.LayoutParams(params.width, params.height, 0.0f)
            }

        }

        addTemplateItemButton.layoutParams = run {
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            layoutParams.setMargins(0, 16, 16, 16)
            layoutParams
        }

        addRestPeriodButton.layoutParams = run {
            val layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            layoutParams.setMargins(16, 16, 0, 16)
            layoutParams
        }

        val params = itemListBinding.root.layoutParams
        itemListBinding.root.layoutParams = LinearLayout.LayoutParams(params.width, params.height, 1.0f)
    }


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        nameBinding.label.hint = "New ${templateTypeToDisplayStr(templateType)} Template ..."
        itemListAdapter.submitList(mutableListOf())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is ItemContainer) {
            //Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a Template with Items (ItemContainer)!")
            return
        }

        nameBinding.label.hint = "Modify ${templateTypeToDisplayStr(templateType)} Template ..."
        nameBinding.input.setText(item.name)
        itemListAdapter.submitList(item.items.toMutableList())

        populateItemsThatCanBeAdded(lifecycle, viewModel)
    }


    private fun templateTypeToDisplayStr(type: ItemType): String {
        return when(type) {
            ItemType.EXERCISE_TEMPLATE -> "Exercise"
            ItemType.SET_TEMPLATE -> "Set"
            ItemType.WORKOUT_TEMPLATE -> "Workout"
            ItemType.PROGRAM_TEMPLATE -> "Program"
            else -> "Unknown"
        }
    }


    private fun populateItemsThatCanBeAdded(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel
    ) {
        lifecycle.launch {
            viewModel.getItemsByType(getTemplateItemType()).collect {
                availableTemplates = it

                val exerciseTemplatesByName = it.map { ex ->
                    ex.name
                }

                availableTemplatesSelectionAdapter =
                    ArrayAdapter(context, R.layout.dropdown_list_item, exerciseTemplatesByName)
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
        val items = itemListAdapter.currentList.toList()

        return when (templateType) {
            ItemType.SET_TEMPLATE -> SetTemplateContent(name, items)
            ItemType.WORKOUT_TEMPLATE -> WorkoutTemplateContent(name, items)
            ItemType.PROGRAM_TEMPLATE -> ProgramTemplateContent(name, items)
            else -> null
        }
    }


    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        if (itemListAdapter.currentList.size == 0) {
            nameBinding.label.error = "At least one item must be added."
            return false
        }

        val items = itemListAdapter.currentList.toList()
        val activeItems =  items.count { it !is RestPeriod }
        if (activeItems == 0) {
            nameBinding.label.error = "Cannot have only Rest Periods."
            return false
        }

        return true
    }
}