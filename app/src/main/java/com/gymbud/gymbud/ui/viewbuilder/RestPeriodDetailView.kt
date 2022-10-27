package com.gymbud.gymbud.ui.viewbuilder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import com.gymbud.gymbud.R
import com.gymbud.gymbud.databinding.LayoutDetailNameBinding
import com.gymbud.gymbud.databinding.LayoutDetailTextFieldBinding
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.RestPeriod
import com.gymbud.gymbud.ui.viewmodel.ItemViewModel

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


    override fun performTransactions(fragmentManager: FragmentManager) {
    }


    override fun populate(
        lifecycle: LifecycleCoroutineScope,
        viewModel: ItemViewModel,
        item: Item
    ) {
        if (item !is RestPeriod) {
            //Log.e(TAG, "Can't populate view because item " + item.name +"(" + item.id + ") is not a RestPeriod!")
            return
        }

        nameBinding.name.text = item.name
        targetRestRangeBinding.text.text = item.getTargetRestPeriodAsString()
    }
}