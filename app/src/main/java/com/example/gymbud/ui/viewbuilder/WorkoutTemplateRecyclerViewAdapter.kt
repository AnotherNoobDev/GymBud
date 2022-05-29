package com.example.gymbud.ui.viewbuilder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailTextFieldBinding
import com.example.gymbud.databinding.LayoutEditListItemButtonBinding
import com.example.gymbud.model.*
import com.google.android.material.button.MaterialButton

// todo lots of duplication with SetTemplateRecyclerViewAdapter atm (basically copy-pasta, only big diff is adapter (setListAdapter))
//  -> can we do better? will other things change in the "final" version to justify keeping them separate? (should still try to remove duplication)
class WorkoutTemplateRecyclerViewAdapter(
    private val functionality: Functionality
) : ListAdapter<Item, WorkoutTemplateRecyclerViewAdapter.ViewHolder>(DiffCallback){

    private var onItemClicked: ((Item) -> Unit)? = null
    fun setOnItemClickedCallback(callback: (Item) -> Unit) {
        onItemClicked = callback
    }

    abstract inner class ViewHolder(protected val rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        protected val setTemplateNameBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        protected val restPeriodLabelBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        protected var onClickedCallback: (() -> Unit)? = null

        init {
            setTemplateNameBinding.icon.setImageResource(R.drawable.ic_equipment_24)
            restPeriodLabelBinding.icon.setImageResource(R.drawable.ic_timer_24)
        }

        protected fun getColorForSetIntensity(intensity: String): Int {
            return when (intensity) {
                SetIntensity.Warmup.toString() -> Color.CYAN // todo should set from resource
                SetIntensity.Working.toString() -> Color.MAGENTA
                else ->  Color.WHITE
            }
        }


        fun populate(item: Item) {
            clear()

            if (item is TaggedItem) {
                if (item.item is SetTemplate) {
                    populateForSetTemplate(item.item, item.tags)
                }
            } else if (item is SetTemplate) {
                populateForSetTemplate(item, mapOf())
            }
            else if (item is RestPeriod) {
                populateForRestPeriod(item)
            }
        }

        protected abstract fun populateForSetTemplate(setTemplate: SetTemplate, tags: Map<TagCategory, Set<String>>)

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
        private val setTemplateIntensityBinding = LayoutDetailTextFieldBinding.inflate(inflater)
        private val restPeriodValueBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        init {
            setTemplateIntensityBinding.icon.isVisible = false

            restPeriodLabelBinding.text.setText(R.string.rest_period_input_label)
            restPeriodValueBinding.icon.isVisible = false
        }

        override fun populateForSetTemplate(setTemplate: SetTemplate, tags: Map<TagCategory, Set<String>>) {
            rootView.addView(setTemplateNameBinding.root)
            rootView.addView(setTemplateIntensityBinding.root)

            setTemplateNameBinding.text.text = setTemplate.name
            setTemplateNameBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params

                setOnClickListener {
                    if (onClickedCallback != null) {
                        onClickedCallback?.let { it1 -> it1() }
                    }
                }
            }

            val intensity = tags[TagCategory.Intensity]?.first() ?: ""

            setTemplateIntensityBinding.text.text = intensity
            setTemplateIntensityBinding.text.setTextColor(getColorForSetIntensity(intensity))
            setTemplateIntensityBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                layoutParams = params
            }
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

        override fun populateForSetTemplate(setTemplate: SetTemplate, tags: Map<TagCategory, Set<String>>) {
            rootView.addView(setTemplateNameBinding.root)

            val intensity = tags[TagCategory.Intensity]?.first() ?: ""

            setTemplateNameBinding.text.setTextColor(getColorForSetIntensity(intensity))
            setTemplateNameBinding.text.text = setTemplate.name

            setTemplateNameBinding.root.apply {
                val params = layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                layoutParams = params
            }

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


    // todo I'm not sure I'm using this as I should --> read the docs!
    companion object DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            // todo implement operator == (also for derived Item!) so that we can do oldItem == newItem
            var same = oldItem.id == newItem.id && oldItem.name == newItem.name

            if (oldItem is TaggedItem && newItem is TaggedItem) {
                same = same && oldItem.tags == newItem.tags
            }

            return same
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