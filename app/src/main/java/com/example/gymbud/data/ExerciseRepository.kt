package com.example.gymbud.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

private const val TAG = "ExerciseRepo"


class ExerciseRepository(
    private val exerciseDao: ExerciseDao
) {
    val exercises: Flow<List<Exercise>> = exerciseDao.getAll()


    suspend fun populateWithDefaults() {
        ExerciseDefaultDatasource.exercises.forEach {
            exerciseDao.insert(it)
        }
    }


    fun retrieveExercise(id: ItemIdentifier): Flow<Exercise?> = exerciseDao.get(id)

    suspend fun fillExerciseContent(exercise: Exercise) {
        val exerciseEntry = exerciseDao.getOnce(exercise.id)

        if (exerciseEntry != null) {
            exercise.name = exerciseEntry.name
            exercise.resistance = exerciseEntry.resistance
            exercise.targetMuscle = exerciseEntry.targetMuscle
            exercise.notes = exerciseEntry.notes
        }
    }


    suspend fun updateExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAllOnce())
            exerciseDao.update(id, validName, description, targetMuscle, resistance)
        }
    }


    suspend fun addExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAllOnce())

            try {
                exerciseDao.insert(Exercise(id, validName, description, targetMuscle, resistance))
            } catch (e: SQLiteConstraintException) {
                Log.e(TAG, "Exercise with id: $id already exists!")
                throw e
            }
        }
    }


    suspend fun removeExercise(id: ItemIdentifier): Boolean {
       return exerciseDao.delete(id) > 0
    }
}