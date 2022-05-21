package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.ExerciseTemplateRepository
import com.example.gymbud.model.*


class ExerciseTemplateViewModel(
    private val exerciseTemplateRepository: ExerciseTemplateRepository
): ViewModel() {

    val exerciseTemplates = exerciseTemplateRepository.exerciseTemplates

    fun addExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        exercise: Exercise,
        targetRepRange: IntRange
    ) {
        exerciseTemplateRepository.addExerciseTemplate(id, name, exercise, targetRepRange)
    }


    fun updateExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        targetRepRange: IntRange
    ) = exerciseTemplateRepository.updateExerciseTemplate(id, name, targetRepRange)


    fun retrieveExerciseTemplate(id: ItemIdentifier): ExerciseTemplate? = exerciseTemplateRepository.retrieveExerciseTemplate(id)

    fun removeExerciseTemplate(id: ItemIdentifier) {
        exerciseTemplateRepository.removeExerciseTemplate(id)
    }
}


class ExerciseTemplateViewModelFactory(private val exerciseTemplateRepository: ExerciseTemplateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseTemplateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseTemplateViewModel(exerciseTemplateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}