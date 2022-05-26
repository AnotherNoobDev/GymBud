package com.example.gymbud.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.databinding.FragmentItemBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import androidx.recyclerview.widget.ListAdapter
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.model.ExerciseTemplate
import com.example.gymbud.model.RestPeriod
import com.example.gymbud.ui.viewbuilder.ExerciseTemplateDetailView
import com.example.gymbud.ui.viewbuilder.ItemViewFactory


class SetTemplateRecyclerViewAdapter(
    private val onItemClicked: (ItemIdentifier) -> Unit,
) : ListAdapter<Item, SetTemplateRecyclerViewAdapter.ViewHolder>(ItemListRecyclerViewAdapter){

    inner class ViewHolder(private val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        private val exerciseTemplateView = ExerciseTemplateDetailView()
        private val restPeriodLabelBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        private val restPeriodValueBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        init {
            exerciseTemplateView.inflate(inflater)

            exerciseTemplateView.targetRepRangeBinding.icon.isVisible = false

            restPeriodLabelBinding.icon.setImageResource(R.drawable.ic_timer_24)
            restPeriodLabelBinding.text.text = "Rest"

            restPeriodValueBinding.icon.isVisible = false
        }


        fun populate(item: Item) {
            clear()

            if (item is ExerciseTemplate) {
                populateForExerciseTemplate(item)
            } else if (item is RestPeriod) {
                populateForRestPeriod(item)
            }
        }

        private fun clear() {
            rootView.removeAllViews()
        }

        private fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(exerciseTemplateView.exerciseBinding.root)
            rootView.addView(exerciseTemplateView.targetRepRangeBinding.root)

            exerciseTemplateView.exerciseBinding.root.apply {
                var params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            exerciseTemplateView.targetRepRangeBinding.root.apply {
                var params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }

            exerciseTemplateView.populate(exerciseTemplate)
        }

        private fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(restPeriodLabelBinding.root)
            rootView.addView(restPeriodValueBinding.root)

            restPeriodLabelBinding.root.apply {
                var params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            restPeriodValueBinding.root.apply {
                var params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }

            restPeriodValueBinding.text.text = restPeriod.getTargetRestPeriodAsString()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            // todo implement operator == (also for derived Item!) so that we can do oldItem == newItem
            return oldItem.id == newItem.id &&
                    oldItem.name == newItem.name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val rootView = RelativeLayout(parent.context)

        return ViewHolder(rootView, layoutInflater)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.populate(getItem(position))
    }
}