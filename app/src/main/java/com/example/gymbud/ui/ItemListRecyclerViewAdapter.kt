package com.example.gymbud.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import com.example.gymbud.databinding.FragmentItemBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier


class ItemListRecyclerViewAdapter(
    private val onItemClicked: (ItemIdentifier) -> Unit,
) : ListAdapter<Item, ItemListRecyclerViewAdapter.ViewHolder>(DiffCallback){

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
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
        return ViewHolder(
            FragmentItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.contentView.text = item.name

        holder.contentView.setOnClickListener {
            onItemClicked(item.id)
        }
    }
}