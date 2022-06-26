package com.example.gymbud.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.databinding.LayoutCalendarDayBinding
import com.example.gymbud.model.DayOfTheMonth
import com.example.gymbud.model.ItemIdentifier


class SessionCalendarRecyclerViewAdapter(
    private val onSessionClicked: (ItemIdentifier) -> Unit
):
    ListAdapter<DayOfTheMonth, SessionCalendarRecyclerViewAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(binding: LayoutCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        val dayOfTheMonthLabel: TextView = binding.dayOfTheMonthLabel
        val dayOfTheMonthContent: TextView = binding.dayOfTheMonthContent
    }

    companion object DiffCallback: DiffUtil.ItemCallback<DayOfTheMonth>() {
        override fun areItemsTheSame(oldItem: DayOfTheMonth, newItem: DayOfTheMonth): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DayOfTheMonth, newItem: DayOfTheMonth): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val holder = ViewHolder(LayoutCalendarDayBinding.inflate(layoutInflater, parent, false))
        holder.itemView.layoutParams.height = parent.measuredHeight / 6
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.dayOfTheMonthLabel.text = item.day.toString()
        holder.dayOfTheMonthContent.text = item.workoutSessionName
        holder.dayOfTheMonthContent.setOnClickListener{
            onSessionClicked(item.workoutSessionsId)
        }
    }
}