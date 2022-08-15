package com.example.gymbud.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.databinding.LayoutDetailExerciseSessionBinding
import com.example.gymbud.model.WeightUnit
import com.example.gymbud.model.WorkoutSessionItem
import com.example.gymbud.model.convertKGtoLB

class SessionExerciseListRecyclerViewAdapter:
    ListAdapter<WorkoutSessionItem.ExerciseSession, SessionExerciseListRecyclerViewAdapter.ViewHolder>(DiffCallback) {

    var displayWeightUnit: WeightUnit = WeightUnit.KG


    inner class ViewHolder(binding: LayoutDetailExerciseSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        val exerciseLabel: TextView = binding.exerciseLabel
        val exerciseValue: TextView = binding.exerciseValue
    }


    companion object DiffCallback: DiffUtil.ItemCallback<WorkoutSessionItem.ExerciseSession>() {
        override fun areItemsTheSame(oldItem: WorkoutSessionItem.ExerciseSession, newItem: WorkoutSessionItem.ExerciseSession): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WorkoutSessionItem.ExerciseSession, newItem: WorkoutSessionItem.ExerciseSession): Boolean {
            return oldItem.getShortName() == newItem.getShortName() &&
                    oldItem.actualResistance == newItem.actualResistance &&
                    oldItem.actualReps == newItem.actualReps
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            LayoutDetailExerciseSessionBinding.inflate(layoutInflater, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.exerciseLabel.text = item.getShortName()

        val exerciseValueStr = "${item.actualReps} x " + when(displayWeightUnit) {
            WeightUnit.KG-> String.format("%.2f kg", item.actualResistance)
            WeightUnit.LB-> String.format("%.2f lb", convertKGtoLB(item.actualResistance))
        }

        holder.exerciseValue.text = exerciseValueStr
    }
}