package com.example.gymbud.ui

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.gymbud.databinding.FragmentItemBinding
import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier

private const val TAG = "ItemList"


class ItemRecyclerViewAdapter(
    private val onItemClicked: (ItemIdentifier) -> Unit,
    private val values: List<Item>
) : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.contentView.text = item.name

        holder.contentView.setOnClickListener {
            onItemClicked(item.id)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}