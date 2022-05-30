package com.example.gymbud.ui.viewbuilder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.databinding.LayoutEditListItemButtonBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.RestPeriod
import com.example.gymbud.model.WorkoutTemplate
import com.google.android.material.button.MaterialButton


// todo lots of duplication with SetTemplateRecyclerViewAdapter atm (basically copy-pasta, only big diff is adapter (setListAdapter))
//  -> can we do better? will other things change in the "final" version to justify keeping them separate? (should still try to remove duplication)
class ProgramTemplateRecyclerViewAdapter(
    private val functionality: Functionality
) : ListAdapter<Item, ProgramTemplateRecyclerViewAdapter.ViewHolder>(DiffCallback) {

        private var onItemClicked: ((Item) -> Unit)? = null
        fun setOnItemClickedCallback(callback: (Item) -> Unit) {
            onItemClicked = callback
        }

        abstract inner class ViewHolder(protected val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
            protected val workoutTemplateNameBinding = LayoutDetailTextFieldBinding.inflate(inflater)
            protected val restPeriodLabelBinding = LayoutDetailTextFieldBinding.inflate(inflater)

            protected var onClickedCallback: (() -> Unit)? = null

            init {
                workoutTemplateNameBinding.icon.setImageResource(R.drawable.ic_equipment_24)
                restPeriodLabelBinding.icon.setImageResource(R.drawable.ic_timer_24)
            }

            fun populate(item: Item) {
                clear()

                if (item is WorkoutTemplate) {
                    populateForWorkoutTemplate(item)
                } else if (item is RestPeriod) {
                    populateForRestDay(item)
                }
            }

            protected abstract fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate)

            protected abstract fun populateForRestDay(restDay: RestPeriod)

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

            override fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate) {
                rootView.addView(workoutTemplateNameBinding.root)

                workoutTemplateNameBinding.text.text = workoutTemplate.name
                workoutTemplateNameBinding.root.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    layoutParams = params

                    setOnClickListener {
                        if (onClickedCallback != null) {
                            onClickedCallback?.let { it1 -> it1() }
                        }
                    }
                }
            }

            override fun populateForRestDay(restDay: RestPeriod) {
                rootView.addView(restPeriodLabelBinding.root)

                restPeriodLabelBinding.text.text = restDay.name
                restPeriodLabelBinding.root.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    layoutParams = params
                }
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

            override fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate) {
                rootView.addView(workoutTemplateNameBinding.root)

                workoutTemplateNameBinding.text.text = workoutTemplate.name
                workoutTemplateNameBinding.root.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    layoutParams = params

                    setOnClickListener {
                        if (onClickedCallback != null) {
                            onClickedCallback?.let { it1 -> it1() }
                        }
                    }
                }

                populateWithRemoveButton()
            }

            override fun populateForRestDay(restDay: RestPeriod) {
                rootView.addView(restPeriodLabelBinding.root)

                restPeriodLabelBinding.text.text = restDay.name
                restPeriodLabelBinding.root.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    layoutParams = params
                }

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
                return if (oldItem == RestPeriod.RestDay) {
                    false
                } else {
                    oldItem == newItem
                }
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