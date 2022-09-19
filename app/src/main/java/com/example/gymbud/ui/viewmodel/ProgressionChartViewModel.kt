package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.ExerciseRepository
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt


enum class ExerciseResultEvaluator {
    OneRepMax,
    MaxWeight
}


enum class TimeWindowLength {
    Week,
    Month,
    Year
}


private fun evaluateExerciseUsingOneRepMax(point: ExerciseProgressionPoint): Number {
    return point.results.maxOf { calculateOneRepMax(it.reps, it.resistance) }
}


private fun evaluateExerciseUsingMaxWeight(point: ExerciseProgressionPoint): Number {
    return point.results.maxOf { it.resistance }
}



class ProgressionChartViewModel(
    private val exerciseRepository: ExerciseRepository
): ViewModel() {
    var exerciseEvaluatorType = ExerciseResultEvaluator.OneRepMax
        set(evaluatorType) {
            field = evaluatorType

            exerciseEvaluator = when(evaluatorType) {
                ExerciseResultEvaluator.OneRepMax -> ::evaluateExerciseUsingOneRepMax
                ExerciseResultEvaluator.MaxWeight -> ::evaluateExerciseUsingMaxWeight
            }
        }

    private var exerciseEvaluator: (point: ExerciseProgressionPoint) -> Number = ::evaluateExerciseUsingOneRepMax

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


    fun generateXYSeries(exerciseProgression: ExerciseProgression): Pair<List<Number>, List<Number>> {
        val timeSeries = mutableListOf<Number>()
        val resultEvalSeries = mutableListOf<Number>()

        var lastAdded = Long.MAX_VALUE
        val oneMonthMs = 30L * 24 * 60 * 60 * 1000

        // points are in date descending order
        exerciseProgression.points.forEach {
            var addPoint = false
            if (timeWindow == TimeWindowLength.Year) {
                if (lastAdded - it.dateMs >= oneMonthMs) {
                    addPoint = true
                }
            } else {
                addPoint = true
            }

            if (addPoint) {
                lastAdded = it.dateMs
                timeSeries.add(it.dateMs)
                resultEvalSeries.add((exerciseEvaluator(it).toDouble() * 100.0).roundToInt() / 100.0) // 2 decimal places
            }
        }

        return Pair(timeSeries, resultEvalSeries)
    }
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