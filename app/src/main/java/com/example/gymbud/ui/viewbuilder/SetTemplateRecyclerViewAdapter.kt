package com.example.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
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
) : ListAdapter<Item, SetTemplateRecyclerViewAdapter.ViewHolder>(DiffCallback), RecyclerViewAdapterWithDragDrop {

    private var onItemClicked: ((Item, Int) -> Unit)? = null
    fun setOnItemClickedCallback(callback: (Item, Int) -> Unit) {
        onItemClicked = callback
    }


    abstract inner class ViewHolder(protected val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        protected val exerciseTemplateBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        protected val restPeriodBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        protected var onViewHolderClicked: ((Int) -> Unit)? = null

        init {
            exerciseTemplateBinding.icon.setImageResource(R.drawable.ic_equipment_24)
            restPeriodBinding.icon.setImageResource(R.drawable.ic_timer_24)
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

        fun setOnViewHolderClickedCallback(onClicked: (absPosition: Int) -> Unit) {
            onViewHolderClicked = onClicked
        }

        private fun clear() {
            rootView.removeAllViews()
        }
    }


    inner class DetailViewHolder(
        rootView: RelativeLayout,
        inflater: LayoutInflater
    ): ViewHolder(rootView, inflater) {

        init {
            exerciseTemplateBinding.iconNavigateTo.visibility = View.VISIBLE
        }

        override fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(this.exerciseTemplateBinding.root)

            this.exerciseTemplateBinding.container.setOnClickListener {
                onViewHolderClicked?.let { it1 -> it1(this.absoluteAdapterPosition) }
            }

            this.exerciseTemplateBinding.text.text = "${exerciseTemplate.exercise.name} (${exerciseTemplate.targetRepRange} reps)"
        }

        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(restPeriodBinding.root)

            restPeriodBinding.text.text = restPeriod.name
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
            rootView.addView(this.exerciseTemplateBinding.root)

            this.exerciseTemplateBinding.text.text = "${exerciseTemplate.exercise.name} (${exerciseTemplate.targetRepRange} reps)"

            populateWithRemoveButton()
        }

        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(restPeriodBinding.root)

            restPeriodBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

            restPeriodBinding.text.text = restPeriod.name

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
                onViewHolderClicked?.let { it1 -> it1(this.absoluteAdapterPosition) }
            }
        }
    }


    companion object DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return if (oldItem is ExerciseTemplate && newItem is ExerciseTemplate && oldItem == newItem) {
                true
            } else {
                oldItem is RestPeriod && newItem is RestPeriod && oldItem == newItem
            }
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
        holder.setOnViewHolderClickedCallback { absPosition ->
            onItemClicked?.let { it(item, absPosition) }
        }
    }


    override fun moveItem(from: Int, to: Int) {
        val newList = currentList.toMutableList()
        val item = newList.removeAt(from)
        newList.add(to, item)

        submitList(newList)
    }
}