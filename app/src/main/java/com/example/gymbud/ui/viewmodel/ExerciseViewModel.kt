package com.example.gymbud.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.ExerciseRepository
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType

class ExerciseViewModel(): ViewModel() {
    private val exerciseRepository = ExerciseRepository()

    private val _exercises = MutableLiveData<List<Exercise>>()
    val exercises: LiveData<List<Exercise>>
        get() = _exercises

    init {
        _exercises.value = exerciseRepository.exercises
    }

    fun addExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        exerciseRepository.addExercise(id, name, resistance, targetMuscle, description)
        _exercises.value = exerciseRepository.exercises
    }


    fun updateExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) = exerciseRepository.updateExercise(id, name, resistance, targetMuscle, description)


    fun retrieveExercise(id: ItemIdentifier): Exercise? = exerciseRepository.retrieveExercise(id)
}


class ExerciseViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}