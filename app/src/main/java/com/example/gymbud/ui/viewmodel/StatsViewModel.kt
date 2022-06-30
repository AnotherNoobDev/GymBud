package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.ExerciseTemplateRepository
import com.example.gymbud.data.repository.SessionsRepository
import com.example.gymbud.model.DayOfTheMonth
import com.example.gymbud.model.ExercisePersonalBestExtendedInfo
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WorkoutSession
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


    suspend fun getPersonalBests(filters: ExerciseFilters): List<ExercisePersonalBestExtendedInfo> {
        val templatesByExercise = exerciseTemplateRepository.exerciseTemplatesByExercise.first()

        return templatesByExercise.mapNotNull { (exercise, templatesForExercise) ->
            val pb = sessionRepository.getExercisePersonalBest(templatesForExercise.map{ it.id }, filters)

            if (pb == null) {
                null
            } else {
                val date = sessionRepository.getWorkoutSessionDate(pb.workoutSessionId)!!
                ExercisePersonalBestExtendedInfo(exercise.name, exercise.id, date, pb)
            }
        }
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