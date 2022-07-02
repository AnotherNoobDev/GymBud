package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.ExerciseRepository
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier
import kotlinx.coroutines.flow.first


enum class ExerciseResultEvaluator {
    OneRepMax,
    MaxWeight
}


enum class TimeWindowLength {
    Week,
    Month,
    Year
}


class ProgressionChartViewModel(
    private val exerciseRepository: ExerciseRepository
): ViewModel() {
    var exerciseEvaluator = ExerciseResultEvaluator.OneRepMax
    var timeWindow = TimeWindowLength.Month

    private var exercise: Exercise? = null


    suspend fun getExerciseOptions() = exerciseRepository.exercises.first()


    suspend fun selectExercise(index: Int): Exercise? {
        val exercises = getExerciseOptions()

        if (exercises.isEmpty()) {
            exercise = null
            return exercise
        }

        exercise = if (index > exercises.size) {
            exercises.last()
        } else {
            exercises[index]
        }

        return exercise
    }


    suspend fun selectExercise(id: ItemIdentifier): Exercise? {
        val exercises = getExerciseOptions()
        exercise = exercises.find { it.id == id }

        return exercise
    }


    fun getSelectedExercise(): Exercise? = exercise
}


class ProgressionChartViewModelFactory(private val exerciseRepository: ExerciseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressionChartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressionChartViewModel(exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}