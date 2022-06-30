package com.example.gymbud.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gymbud.databinding.LayoutPersonalBestExerciseBinding
import com.example.gymbud.model.ExercisePersonalBestExtendedInfo
import com.example.gymbud.utility.TimeFormatter
import java.util.*



class PersonalBestsRecyclerViewAdapter(
    //private val onPersonalBestClicked: (ItemIdentifier) -> Unit
): ListAdapter<ExercisePersonalBestExtendedInfo, PersonalBestsRecyclerViewAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(binding: LayoutPersonalBestExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        val date: TextView = binding.date
        val exercise: TextView = binding.exercise
        val reps: TextView = binding.reps
        val resistance: TextView = binding.resistance
    }

    companion object DiffCallback: DiffUtil.ItemCallback<ExercisePersonalBestExtendedInfo>() {
        override fun areItemsTheSame(oldItem: ExercisePersonalBestExtendedInfo, newItem: ExercisePersonalBestExtendedInfo): Boolean {
            return oldItem.exerciseId == newItem.exerciseId
        }

        override fun areContentsTheSame(oldItem: ExercisePersonalBestExtendedInfo, newItem: ExercisePersonalBestExtendedInfo): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(LayoutPersonalBestExerciseBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.date.text = TimeFormatter.getFormattedDateDDMMYYYY(Date(item.dateMs))
        holder.exercise.text = item.exerciseName
        holder.reps.text = item.reps.toString() + " x"
        holder.resistance.text = item.resistance

        /*
        // todo what info do we need to pass here to switch to graph view when the PB is clicked
        holder.itemView.setOnClickListener{

            onPersonalBestClicked(item.workoutSessionId)
        }
         */
    }
}