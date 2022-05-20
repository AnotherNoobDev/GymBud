package com.example.gymbud.data

import android.util.Log
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier
import kotlinx.coroutines.flow.*

private const val TAG = "ExerciseDetail"


class ExerciseRepository {
    private val _exercises: MutableStateFlow<List<Exercise>> = MutableStateFlow(ExerciseDefaultDatasource.exercises)
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()


    fun retrieveExercise(id: ItemIdentifier): Exercise? = _exercises.value.find{ it.id == id }

    fun updateExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        val exercise = retrieveExercise(id)
        exercise?.name = name
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
            // todo -> get a new id?
            return
        }

        val newExercises = _exercises.value.toMutableList()
        newExercises.add(Exercise(id, name, description, targetMuscle, resistance))
        _exercises.value = newExercises
    }

    fun removeExercise(id: ItemIdentifier) {
        val exercise = retrieveExercise(id)

        val newExercises = _exercises.value.toMutableList()
        newExercises.remove(exercise)
        _exercises.value = newExercises
    }
}