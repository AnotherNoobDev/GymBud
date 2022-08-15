package com.example.gymbud.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.gymbud.data.datasource.database.ExerciseDao
import com.example.gymbud.data.datasource.defaults.ExerciseDefaultDatasource
import com.example.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        val exerciseEntry = exerciseDao.get(exercise.id).first()

        if (exerciseEntry != null) {
            exercise.name = exerciseEntry.name
            exercise.targetMuscle = exerciseEntry.targetMuscle
            exercise.notes = exerciseEntry.notes
        }
    }


    suspend fun updateExercise(
        id: ItemIdentifier,
        name: String,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAll().first())
            exerciseDao.update(id, validName, description, targetMuscle)
        }
    }


    suspend fun addExercise(
        id: ItemIdentifier,
        name: String,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAll().first())

            try {
                exerciseDao.insert(Exercise(id, validName, description, targetMuscle))
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