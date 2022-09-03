package com.example.gymbud.ui.viewbuilder

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.databinding.LayoutEditListItemButtonBinding
import com.example.gymbud.model.*
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.collections.ArrayList


private const val  MAX_ITEM_LENGTH_EMS = 12

//private const val TAG = "TemplateWithItemsRVA"


class TemplateWithItemsRecyclerViewAdapter(
    context: Context,
    private val functionality: Functionality
) : RecyclerView.Adapter<TemplateWithItemsRecyclerViewAdapter.ViewHolder>(), RecyclerViewAdapterWithDragDrop {

    val currentList = mutableListOf<Item>()
    private val diffCallback = DiffCallback(currentList, ArrayList())

    private val colorWarmup: Int
    private val colorWorking: Int
    private val colorDefault: Int


    init {
        val theme = context.theme
        val themeVal = TypedValue()

        theme.resolveAttribute(R.attr.intensityWarmupColor, themeVal, true)
        colorWarmup = themeVal.data

        theme.resolveAttribute(R.attr.intensityWorkingColor, themeVal, true)
        colorWorking = themeVal.data

        theme.resolveAttribute(R.attr.intensityDefaultColor, themeVal, true)
        colorDefault = themeVal.data
    }


    private var onItemClicked: ((Item, Int) -> Unit)? = null
    fun setOnItemClickedCallback(callback: (Item, Int) -> Unit) {
        onItemClicked = callback
    }


    abstract inner class ViewHolder(protected val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        protected val itemBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        protected var onViewHolderClicked: ((Int) -> Unit)? = null
        fun setOnViewHolderClickedCallback(onClicked: (absPosition: Int) -> Unit) {
            onViewHolderClicked = onClicked
        }

        protected fun getColorForSetIntensity(intensity: String): Int {
            return when (intensity) {
                SetIntensity.Warmup.toString() -> colorWarmup
                SetIntensity.Working.toString() -> colorWorking
                else ->  colorDefault
            }
        }


        fun populate(item: Item) {
            clear()

            when (item) {
                is ExerciseTemplate -> populateForExerciseTemplate(item)
                is TaggedItem -> if (item.item is SetTemplate) populateForSetTemplate(item.item, item.tags)
                is SetTemplate -> populateForSetTemplate(item, mapOf())
                is WorkoutTemplate -> populateForWorkoutTemplate(item)
                is RestPeriod -> populateForRestPeriod(item)
            }
        }


        protected abstract fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate)

        protected abstract fun populateForSetTemplate(setTemplate: SetTemplate, tags: Tags)

        protected abstract fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate)

        protected abstract fun populateForRestPeriod(restPeriod: RestPeriod)


        private fun clear() {
            rootView.removeAllViews()
        }
    }


    inner class DetailViewHolder(
        rootView: RelativeLayout,
        inflater: LayoutInflater
    ): ViewHolder(rootView, inflater) {
        override fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                text.text = "${exerciseTemplate.exercise.name} (${exerciseTemplate.targetRepRange} reps)"
                icon.setImageResource(R.drawable.ic_equipment_24)
                iconNavigateTo.visibility = View.VISIBLE
                container.setOnClickListener {
                    onViewHolderClicked?.let { it1 -> it1(absoluteAdapterPosition) }
                }
            }
        }


        override fun populateForSetTemplate(setTemplate: SetTemplate, tags: Tags) {
            rootView.addView(itemBinding.root)

            val intensity = tags[TagCategory.Intensity]?.first() ?: ""

            itemBinding.apply {
                text.text = "${setTemplate.name} ($intensity)"
                text.setTextColor(getColorForSetIntensity(intensity))
                icon.setImageResource(R.drawable.ic_equipment_24)
                iconNavigateTo.visibility = View.VISIBLE
                container.setOnClickListener {
                    onViewHolderClicked?.let { it1 -> it1(absoluteAdapterPosition) }
                }
            }
        }


        override fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                text.text = workoutTemplate.name
                icon.setImageResource(R.drawable.ic_equipment_24)
                iconNavigateTo.visibility = View.VISIBLE
                container.setOnClickListener {
                    onViewHolderClicked?.let { it1 -> it1(absoluteAdapterPosition) }
                }
            }
        }


        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                icon.setImageResource(R.drawable.ic_timer_24)
                text.text = restPeriod.name
                text.setTextColor(getColorForSetIntensity(""))
                iconNavigateTo.visibility = View.GONE
                container.setOnClickListener {}
            }
        }
    }


    inner class EditViewHolder(
        rootView: RelativeLayout,
        inflater: LayoutInflater
    ): ViewHolder(rootView, inflater) {
        private val removeItemButton: LayoutEditListItemButtonBinding = LayoutEditListItemButtonBinding.inflate(inflater)

        init {
            itemBinding.text.isSingleLine = false
            itemBinding.text.maxEms = MAX_ITEM_LENGTH_EMS

            removeItemButton.button.setIconResource(R.drawable.ic_remove_24)
            removeItemButton.button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
            removeItemButton.button.iconPadding = 0
        }

        override fun populateForExerciseTemplate(exerciseTemplate: ExerciseTemplate) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                icon.setImageResource(R.drawable.ic_equipment_24)
                text.text = "${exerciseTemplate.exercise.name} (${exerciseTemplate.targetRepRange} reps)"
            }

            populateWithRemoveButton()
        }


        override fun populateForSetTemplate(setTemplate: SetTemplate, tags: Tags) {
            rootView.addView(itemBinding.root)

           val intensity = tags[TagCategory.Intensity]?.first() ?: ""

            itemBinding.apply {
                text.text = "${setTemplate.name} ($intensity)"
                text.setTextColor(getColorForSetIntensity(intensity))
                icon.setImageResource(R.drawable.ic_equipment_24)
            }

            populateWithRemoveButton()
        }


        override fun populateForWorkoutTemplate(workoutTemplate: WorkoutTemplate) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                icon.setImageResource(R.drawable.ic_equipment_24)
                text.text = workoutTemplate.name
            }

            populateWithRemoveButton()
        }


        override fun populateForRestPeriod(restPeriod: RestPeriod) {
            rootView.addView(itemBinding.root)

            itemBinding.apply {
                text.text = restPeriod.name
                text.setTextColor(getColorForSetIntensity(""))
                icon.setImageResource(R.drawable.ic_timer_24)
            }

            populateWithRemoveButton()
        }


        private fun populateWithRemoveButton() {
            rootView.addView(removeItemButton.root)

            removeItemButton.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.addRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams = params
            }

            removeItemButton.button.setOnClickListener {
                currentList.removeAt(this.absoluteAdapterPosition)
                notifyItemRemoved(this.absoluteAdapterPosition)
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
        val item = currentList[position]
        holder.populate(item)
        holder.setOnViewHolderClickedCallback { absPosition ->
            onItemClicked?.let { it(item, absPosition) }
        }
    }


    inner class DiffCallback(
        private val oldList: List<Item>,
        var newList: List<Item>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            return if (oldItem is TaggedItem && newItem is TaggedItem) {
                oldItem == newItem
            } else {
                oldItem.id == newItem.id
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return areContentsTheSame(oldItem, newItem)
        }

        private fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return if (oldItem is TaggedItem && newItem is TaggedItem && oldItem == newItem) {
                true
            } else if (oldItem is ExerciseTemplate && newItem is ExerciseTemplate && oldItem == newItem) {
                true
            } else if (oldItem is SetTemplate && newItem is SetTemplate && oldItem == newItem) {
                true
            } else if (oldItem is WorkoutTemplate && newItem is WorkoutTemplate && oldItem == newItem) {
                true
            } else if (oldItem is ProgramTemplate && newItem is ProgramTemplate && oldItem == newItem) {
                true
            } else {
                oldItem is RestPeriod && newItem is RestPeriod && oldItem == newItem
            }
        }
    }


    fun submitList(updatedList: List<Item>) {
        diffCallback.newList = updatedList
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        currentList.clear()
        currentList.addAll(updatedList)
        diffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int {
        return currentList.size
    }


    override fun moveItem(from: Int, to: Int) {
        Collections.swap(currentList, from, to)
        notifyItemMoved(from, to)
    }
}