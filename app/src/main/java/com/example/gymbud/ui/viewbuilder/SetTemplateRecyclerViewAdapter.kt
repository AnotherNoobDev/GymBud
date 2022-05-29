package com.example.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.model.Item
import androidx.recyclerview.widget.ListAdapter
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.databinding.LayoutEditListItemButtonBinding
import com.example.gymbud.model.ExerciseTemplate
import com.example.gymbud.model.RestPeriod
import com.google.android.material.button.MaterialButton


class SetTemplateRecyclerViewAdapter(
    private val functionality: Functionality
) : ListAdapter<Item, SetTemplateRecyclerViewAdapter.ViewHolder>(DiffCallback){

    private var onItemClicked: ((Item) -> Unit)? = null
    fun setOnItemClickedCallback(callback: (Item) -> Unit) {
        onItemClicked = callback
    }

    abstract inner class ViewHolder(protected val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        protected val exerciseTemplateView = ExerciseTemplateDetailView()
        protected val restPeriodLabelBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        protected var onClickedCallback: (() -> Unit)? = null

        init {
            exerciseTemplateView.inflate(inflater)

            restPeriodLabelBinding.icon.setImageResource(R.drawable.ic_timer_24)
        }

        fun populate(item: Item) {
            clear()

            if (item is ExerciseTemplate) {
                populateForExerciseTemplate(item)
            } else if (item is RestPeriod) {
                populateForRestPeriod(item)
            }
        }

        protected abstract fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate)

        protected abstract fun populateForRestPeriod(restPeriod: RestPeriod)

        fun setOnClickListener(onClicked: () -> Unit) {
            onClickedCallback = onClicked
        }

        private fun clear() {
            rootView.removeAllViews()
        }


    }

    inner class DetailViewHolder(
        rootView: RelativeLayout,
        inflater: LayoutInflater
    ): ViewHolder(rootView, inflater) {
        private val restPeriodValueBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        init {
            exerciseTemplateView.targetRepRangeBinding.icon.isVisible = false

            restPeriodLabelBinding.text.setText(R.string.rest_period_input_label)

            restPeriodValueBinding.icon.isVisible = false
        }

        override fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(exerciseTemplateView.exerciseBinding.root)
            rootView.addView(exerciseTemplateView.targetRepRangeBinding.root)

            exerciseTemplateView.exerciseBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params

                setOnClickListener {
                    if (onClickedCallback != null) {
                        onClickedCallback?.let { it1 -> it1() }
                    }
                }
            }

            exerciseTemplateView.targetRepRangeBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }

            exerciseTemplateView.populate(exerciseTemplate)
        }

        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(restPeriodLabelBinding.root)
            rootView.addView(restPeriodValueBinding.root)

            restPeriodLabelBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            restPeriodValueBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }

            restPeriodValueBinding.text.text = restPeriod.getTargetRestPeriodAsString()
        }
    }

    inner class EditViewHolder(
        rootView: RelativeLayout,
        inflater: LayoutInflater
    ): ViewHolder(rootView, inflater) {
        private val removeItemButton: LayoutEditListItemButtonBinding = LayoutEditListItemButtonBinding.inflate(inflater)

        init {
            removeItemButton.button.setIconResource(R.drawable.ic_remove_24)
            removeItemButton.button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            removeItemButton.button.iconPadding = 0
        }

        override fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(exerciseTemplateView.exerciseBinding.root)

            exerciseTemplateView.exerciseBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            exerciseTemplateView.populate(exerciseTemplate)

            populateWithRemoveButton()
        }

        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(restPeriodLabelBinding.root)

            restPeriodLabelBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            restPeriodLabelBinding.text.text = restPeriod.name

            populateWithRemoveButton()
        }

        private fun populateWithRemoveButton() {
            rootView.addView(removeItemButton.root)

            removeItemButton.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }

            removeItemButton.button.setOnClickListener {
                onClickedCallback?.let { it1 -> it1() }
            }
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

        return when(functionality) {
            Functionality.Detail -> DetailViewHolder(
                rootView,
                layoutInflater
            )
            Functionality.Edit -> EditViewHolder(
                rootView,
                layoutInflater
            )
        }
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.populate(item)
        holder.setOnClickListener {
            onItemClicked?.let { it(item) }
        }
    }
}