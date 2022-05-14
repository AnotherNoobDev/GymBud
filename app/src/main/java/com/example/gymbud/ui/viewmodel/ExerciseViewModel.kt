package com.example.gymbud.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.ExerciseRepository
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier

class ExerciseViewModel(app: Application): ViewModel() {
    private val exerciseRepository = ExerciseRepository()

    fun retrieveExercise(id: ItemIdentifier): Exercise? = exerciseRepository.retrieveExercise(id)


    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ExerciseViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}