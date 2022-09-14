package com.example.gymbud.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.R
import com.example.gymbud.databinding.LayoutDetailExerciseSessionBinding
import com.example.gymbud.model.*
import kotlin.math.abs

class SessionExerciseListRecyclerViewAdapter (private val showProgression: Boolean, private val showNotes: Boolean):
    ListAdapter<WorkoutSessionItem.ExerciseSession, SessionExerciseListRecyclerViewAdapter.ViewHolder>(DiffCallback) {

    var displayWeightUnit: WeightUnit = WeightUnit.KG


    inner class ViewHolder(binding: LayoutDetailExerciseSessionBinding) : RecyclerView.ViewHolder(binding.root) {
        val exerciseLabel: TextView = binding.exerciseLabel
        val exerciseValue: TextView = binding.exerciseValue
        val exerciseProgression: ImageView = binding.progressionIcon
        val exercisePrevValue: TextView = binding.exercisePrevValue
        val exercisePrevLabel: TextView = binding.exercisePrevLabel
        val notes: TextView = binding.notes
    }


    companion object DiffCallback: DiffUtil.ItemCallback<WorkoutSessionItem.ExerciseSession>() {
        override fun areItemsTheSame(oldItem: WorkoutSessionItem.ExerciseSession, newItem: WorkoutSessionItem.ExerciseSession): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WorkoutSessionItem.ExerciseSession, newItem: WorkoutSessionItem.ExerciseSession): Boolean {
            return oldItem.getShortName() == newItem.getShortName() &&
                    oldItem.actualResistance == newItem.actualResistance &&
                    oldItem.actualReps == newItem.actualReps &&
                    oldItem.notes == newItem.notes
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
        val intensity = item.tags?.get(TagCategory.Intensity)?.first() ?: ""
        if (intensity == "") {
            holder.exerciseLabel.text = item.getShortName()
        } else {
            holder.exerciseLabel.text = "${item.getShortName()} ($intensity)"
        }

        holder.exerciseValue.text = getExerciseValueStr(item.actualReps, item.actualResistance)

        val prevReps = item.getPreviousReps()
        val prevResistance = item.getPreviousResistance()

        val displayProgression = showProgression && prevReps != null && prevResistance != null

        if (displayProgression) {
            holder.exerciseProgression.visibility = View.VISIBLE
            holder.exercisePrevValue.visibility = View.VISIBLE
            holder.exercisePrevLabel.visibility = View.VISIBLE

            holder.exercisePrevValue.text = getExerciseValueStr(prevReps!!, prevResistance!!)

            val prev1RM = calculateOneRepMax(prevReps, prevResistance)
            val today1RM = calculateOneRepMax(item.actualReps, item.actualResistance)
            when {
                today1RM > prev1RM -> {
                    holder.exerciseProgression.setImageResource(R.drawable.ic_arrow_upward_24)
                }
                abs(today1RM - prev1RM) < 0.1 -> {
                    holder.exerciseProgression.setImageResource(R.drawable.ic_remove_24)
                }
                else -> {
                    holder.exerciseProgression.setImageResource(R.drawable.ic_arrow_downward_24)
                }
            }
        } else {
            holder.exerciseProgression.visibility = View.GONE
            holder.exercisePrevValue.visibility = View.GONE
            holder.exercisePrevLabel.visibility = View.GONE
        }

        val displayNotes = showNotes && !displayProgression && item.notes.isNotEmpty()

        if (displayNotes) {
            holder.notes.visibility = View.VISIBLE
            holder.notes.text = item.notes
        } else {
            holder.notes.visibility = View.GONE
        }
    }


    private fun getExerciseValueStr(reps: Int, resistance: Double): String {
        return "$reps x " + when(displayWeightUnit) {
            WeightUnit.KG-> String.format("%.2f kg", resistance)
            WeightUnit.LB-> String.format("%.2f lb", convertKGtoLB(resistance))
        }
    }
}