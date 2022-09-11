package com.example.gymbud.ui.live_session

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutLiveSessionOverviewItemBinding
import com.example.gymbud.model.WorkoutSessionItem


class LiveSessionOverviewRecyclerViewAdapter(
    context: Context,
    private val onItemClicked: (Int) -> Unit
    ): ListAdapter<WorkoutSessionItem, LiveSessionOverviewRecyclerViewAdapter.ViewHolder>(DiffCallback) {

    private var atItemInLiveSession = -1
    private var progressedToItemInLiveSession = -1

    private val colorVisited: Int
    private val colorCurrent: Int
    private val colorNotVisited: Int

    init {
        val theme = context.theme
        val themeVal = TypedValue()

        theme.resolveAttribute(R.attr.liveSessionOverviewVisitedItemColor, themeVal, true)
        colorVisited = themeVal.data

        theme.resolveAttribute(R.attr.liveSessionOverviewCurrentItemColor, themeVal, true)
        colorCurrent = themeVal.data

        theme.resolveAttribute(R.attr.liveSessionOverviewNotVisitedItemColor, themeVal, true)
        colorNotVisited = themeVal.data
    }


    inner class ViewHolder(binding: LayoutLiveSessionOverviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
    }

    companion object DiffCallback: DiffUtil.ItemCallback<WorkoutSessionItem>() {
        override fun areItemsTheSame(oldItem: WorkoutSessionItem, newItem: WorkoutSessionItem): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: WorkoutSessionItem, newItem: WorkoutSessionItem): Boolean {
            return false //oldItem.type == newItem.type && oldItem.hint == newItem.hint
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSessionOverviewRecyclerViewAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutLiveSessionOverviewItemBinding.inflate(layoutInflater, parent, false))
    }


    override fun onBindViewHolder(holder: LiveSessionOverviewRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        holder.name.text = item.hint

        val color = when {
            position == atItemInLiveSession -> {
                colorCurrent
            }
            position <= progressedToItemInLiveSession -> {
                colorVisited
            }
            else -> {
                colorNotVisited
            }
        }

        holder.name.setTextColor(color)

        // ignore clicks on Workout Items we haven't visited yet
        if (position <= progressedToItemInLiveSession) {
            holder.name.setOnClickListener {
                onItemClicked(position)
            }
        } else {
            holder.name.setOnClickListener {
            }
        }
    }


    fun update(items: List<WorkoutSessionItem>, atItem: Int, progressedToItem: Int) {
        atItemInLiveSession = atItem
        progressedToItemInLiveSession = progressedToItem
        submitList(items)
    }
}