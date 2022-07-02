package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.ExerciseTemplateRepository
import com.example.gymbud.data.repository.SessionsRepository
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.first

class StatsViewModel (
    private val sessionRepository: SessionsRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
): ViewModel() {
    suspend fun getSession(workoutSessionId: ItemIdentifier): WorkoutSession? {
        return sessionRepository.getWorkoutSession(workoutSessionId)
    }


    suspend fun removeSession(workoutSessionId: ItemIdentifier): Boolean {
        return sessionRepository.removeSession(workoutSessionId)
    }


    suspend fun getSessionsByMonth(year: Int, month: Int, daySpan: Int): List<DayOfTheMonth> {
        return sessionRepository.getSessionsByMonth(year, month, daySpan)
    }


    suspend fun getPersonalBests(filters: ExerciseFilters): List<ExercisePersonalBest> {
        val templatesByExercise = exerciseTemplateRepository.exerciseTemplatesByExercise.first()

        return templatesByExercise.mapNotNull { (exercise, templatesForExercise) ->
            val pb = sessionRepository.getExercisePersonalBest(templatesForExercise.map{ it.id }, filters)

            if (pb == null) {
                null
            } else {
                val date = sessionRepository.getWorkoutSessionDate(pb.workoutSessionId)!!
                ExercisePersonalBest(exercise.name, exercise.id, date, pb)
            }
        }
    }

    // todo very trivial at the moment, just return all filtered period
    // but could be optimized by implementing a moving time window
    suspend fun getExerciseProgression(exercise: Exercise, filters: ExerciseFilters): ExerciseProgression? {
        val templatesForExercise = exerciseTemplateRepository.exerciseTemplatesByExercise.first().find { it.first.id == exercise.id }
            ?: return null

        val results = sessionRepository.getExerciseResults(templatesForExercise.second.map { it.id }, filters)

        val progressionPoints = results.map {
            ExerciseProgressionPoint(it, sessionRepository.getWorkoutSessionDate(it.workoutSessionId)!!)
        }.sortedByDescending { it.dateMs }


        return ExerciseProgression(exercise.name, exercise.id, progressionPoints)
    }
}


class StatsViewModelFactory(private val sessionRepository: SessionsRepository,
                            private val exerciseTemplateRepository: ExerciseTemplateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(sessionRepository, exerciseTemplateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}