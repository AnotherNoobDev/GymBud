package com.example.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailNameBinding
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.RestPeriod
import com.example.gymbud.ui.viewmodel.ItemViewModel

private const val TAG = "RestPeriodDV"

class RestPeriodDetailView: ItemView {
    private var _nameBinding: LayoutDetailNameBinding? = null
    private val nameBinding get() = _nameBinding!!

    private var _targetRestRangeBinding: LayoutDetailTextFieldBinding? = null
    private val targetRestRangeBinding get() = _targetRestRangeBinding!!


    override fun inflate(inflater: LayoutInflater): List<View> {
        _nameBinding = LayoutDetailNameBinding.inflate(inflater)
        _targetRestRangeBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        targetRestRangeBinding.icon.setImageResource(R.drawable.ic_timer_24)

        return listOf(
            nameBinding.root,
            targetRestRangeBinding.root
        )
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is RestPeriod) {
            Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a RestPeriod!")
            return
        }

        nameBinding.name.text = item.name
        targetRestRangeBinding.text.text = item.getTargetRestPeriodAsString()
    }
}