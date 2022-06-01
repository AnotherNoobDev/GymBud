package com.example.gymbud.data

import android.util.Log
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.*

private const val TAG = "ExerciseDetail"

// todo can we template all these repositories?
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


    fun retrieveExerciseTemplate(id: ItemIdentifier): ExerciseTemplate? = _exercisesTemplates.value.find{ it.id == id }


    fun updateExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        targetRepRange: IntRange
    ) {
        val validName = getValidName(id, name, _exercisesTemplates.value)

        val exerciseTemplate = retrieveExerciseTemplate(id)
        exerciseTemplate?.name = validName
        exerciseTemplate?.targetRepRange = targetRepRange
    }


    fun addExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        exercise: Exercise,
        targetRepRange: IntRange
    ) {
        val exerciseTemplate = retrieveExerciseTemplate(id)
        if (exerciseTemplate != null) {
            Log.e(TAG, "ExerciseTemplate with id: " + id + "already exists!")
            // todo -> get a new id?
            return
        }

        val validName = getValidName(id, name, _exercisesTemplates.value)

        val newExercises = _exercisesTemplates.value.toMutableList()
        newExercises.add(ExerciseTemplate(id, validName, exercise, targetRepRange))
        _exercisesTemplates.value = newExercises
    }

    fun removeExerciseTemplate(id: ItemIdentifier): Boolean {
        val exerciseTemplate = retrieveExerciseTemplate(id)

        val newExercisesTemplates = _exercisesTemplates.value.toMutableList()
        val removed = newExercisesTemplates.remove(exerciseTemplate)

        if (removed) {
            _exercisesTemplates.value = newExercisesTemplates
        }

        return removed
    }
}