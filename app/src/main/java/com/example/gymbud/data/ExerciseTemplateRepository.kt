package com.example.gymbud.data

import com.example.gymbud.model.ExerciseTemplate
import kotlinx.coroutines.flow.*

class ExerciseTemplateRepository(
    private val exerciseRepository: ExerciseRepository
) {
    private val _exercisesTemplates: MutableStateFlow<List<ExerciseTemplate>> = MutableStateFlow(ExerciseDefaultDatasource.exerciseTemplatesForHypertrophy)
    val exerciseTemplates: Flow<List<ExerciseTemplate>> =
        _exercisesTemplates.asStateFlow().combine(exerciseRepository.exercises) { exTemplateList, exList ->
            exTemplateList.filter {
                exList.contains(it.exercise)
            }
        }

    // needs to be called whenever an exercise is removed
    // todo how can we make sure this is always done when an exercise gets removed
    // maybe not actually needed --> since ExerciseTemplate depends on exercise, the delete should cascade in the database implementation
    fun validate() {
        val exercises = exerciseRepository.exercises.value
        val templates = _exercisesTemplates.value

        _exercisesTemplates.value = templates.filter {
            exercises.contains(it.exercise)
        }
    }

    // todo add, remove, update
}