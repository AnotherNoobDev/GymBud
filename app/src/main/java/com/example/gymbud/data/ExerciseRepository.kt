package com.example.gymbud.data

import android.util.Log
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.*

private const val TAG = "ExerciseRepository"


class ExerciseRepository {
    private val _exercises: MutableStateFlow<List<Exercise>> = MutableStateFlow(ExerciseDefaultDatasource.exercises)
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()


    fun retrieveExercise(id: ItemIdentifier): Exercise? = _exercises.value.find{ it.id == id }

    fun retrieveExercise(name: String): Exercise? = _exercises.value.find{ it.name == name }

    fun updateExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        val validName = getValidName(id, name, _exercises.value)

        val exercise = retrieveExercise(id)
        exercise?.name = validName
        exercise?.resistance = resistance
        exercise?.targetMuscle = targetMuscle
        exercise?.description = description
    }

    fun addExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        val exercise = retrieveExercise(id)
        if (exercise != null) {
            Log.e(TAG, "Exercise with id: " + id + "already exists!")
            assert(false)
        }

        val validName = getValidName(id, name, _exercises.value)

        val newExercises = _exercises.value.toMutableList()
        newExercises.add(Exercise(id, validName, description, targetMuscle, resistance))
        _exercises.value = newExercises
    }

    fun removeExercise(id: ItemIdentifier): Boolean {
        val exercise = retrieveExercise(id)

        val newExercises = _exercises.value.toMutableList()
        val removed = newExercises.remove(exercise)

        if (removed) {
            _exercises.value = newExercises
        }

        return removed
    }
}