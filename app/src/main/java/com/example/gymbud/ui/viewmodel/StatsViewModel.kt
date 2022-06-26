package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.SessionsRepository
import com.example.gymbud.model.DayOfTheMonth
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WorkoutSession

class StatsViewModel (
    private val sessionRepository: SessionsRepository
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
}


class StatsViewModelFactory(private val sessionRepository: SessionsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}