package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutEditRangeSliderBinding
import com.example.gymbud.databinding.LayoutEditTextFieldBinding
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ItemViewModel


private const val TAG = "RestPeriodEV"


class RestPeriodEditView(
    private val context: Context
): EditItemView {
    private var _nameBinding: LayoutEditTextFieldBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _restRangeBinding: LayoutEditRangeSliderBinding? = null
    private val restRangeBinding get() = _restRangeBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutEditTextFieldBinding.inflate(inflater)
        _restRangeBinding = LayoutEditRangeSliderBinding.inflate(inflater)

        nameBinding.label.hint = context.getString(R.string.item_name)
        nameBinding.input.setOnClickListener {
            nameBinding.label.error = null
        }


        restRangeBinding.label.text = "Rest (in seconds)"
        restRangeBinding.input.valueFrom = 0.0f
        restRangeBinding.input.valueTo = 60.0f * 10 // longer than 10 minutes doesn't make sense, or?

        return listOf(
            nameBinding.root,
            restRangeBinding.root
        )
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is RestPeriod) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a rest period!")
            return
        }

        nameBinding.input.setText(item.name,  TextView.BufferType.SPANNABLE)
        restRangeBinding.input.values = mutableListOf(
            item.targetRestPeriodSec.first.toFloat(),
            item.targetRestPeriodSec.last.toFloat(),
        )
    }


    override fun populateForNewItem(lifecycle: LifecycleCoroutineScope, viewModel: ItemViewModel) {
        restRangeBinding.input.values = mutableListOf(60.0f, 120.0f)
    }


    override fun getContent(): ItemContent? {
        if (!validateInput()) {
            return null
        }

        val name = nameBinding.input.text.toString()

        val targetRestRange = IntRange(
            restRangeBinding.input.values[0].toInt(),
            restRangeBinding.input.values[1].toInt(),
        )

        return RestPeriodContent(name, targetRestRange)
    }


    private fun validateInput(): Boolean {
        if (nameBinding.input.text.isNullOrEmpty()) {
            nameBinding.label.error = context.getString(R.string.item_name_err)
            return false
        }

        return true
    }
}