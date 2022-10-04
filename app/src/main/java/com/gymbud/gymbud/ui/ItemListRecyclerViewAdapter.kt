package com.gymbud.gymbud.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.gymbud.gymbud.databinding.LayoutDetailTextFieldBinding
import com.gymbud.gymbud.model.Item
import com.gymbud.gymbud.model.ItemIdentifier


class ItemListRecyclerViewAdapter(
    private val onItemClicked: (ItemIdentifier) -> Unit,
) : ListAdapter<Item, ItemListRecyclerViewAdapter.ViewHolder>(DiffCallback){

    inner class ViewHolder(rootView: RelativeLayout, inflater: LayoutInflater) : RecyclerView.ViewHolder(rootView) {
        val itemBinding = LayoutDetailTextFieldBinding.inflate(inflater)

        init {
            itemBinding.icon.visibility = View.GONE
            itemBinding.iconNavigateTo.visibility = View.VISIBLE
            rootView.addView(itemBinding.root)
        }


        override fun toString(): String {
            return super.toString() + " '" + itemBinding.text + "'"
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
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
        val item = getItem(position)
        holder.itemBinding.text.text = item.name

        holder.itemBinding.root.setOnClickListener {
            onItemClicked(item.id)
        }
    }
}