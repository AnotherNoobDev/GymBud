package com.gymbud.gymbud.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gymbud.gymbud.data.datasource.database.ExerciseDao
import com.gymbud.gymbud.data.datasource.defaults.ExerciseDefaultDatasource
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

private const val TAG = "ExerciseRepo"


class ExerciseRepository(
    private var exerciseDao: ExerciseDao
) {
    fun setDao(exerciseDao: ExerciseDao) {
        this.exerciseDao = exerciseDao
    }


    val exercises: Flow<List<Exercise>> = exerciseDao.getAll()


    suspend fun populateWithDefaults() {
        ExerciseDefaultDatasource.exercises.forEach {
            exerciseDao.insert(it)
        }
    }


    fun retrieveExercise(id: ItemIdentifier): Flow<Exercise?> = exerciseDao.get(id)


    suspend fun hasExerciseWithSameContent(pendingEntry: Exercise): Exercise? =
        exerciseDao.hasExerciseWithSameContent(pendingEntry.name, pendingEntry.notes, pendingEntry.targetMuscle, pendingEntry.videoTutorial)


    suspend fun fillExerciseContent(exercise: Exercise) {
        val exerciseEntry = exerciseDao.get(exercise.id).first()

        // todo I don't like this.. seems error prone
        if (exerciseEntry != null) {
            exercise.name = exerciseEntry.name
            exercise.targetMuscle = exerciseEntry.targetMuscle
            exercise.notes = exerciseEntry.notes
            exercise.videoTutorial = exerciseEntry.videoTutorial
        }
    }


    suspend fun updateExercise(
        id: ItemIdentifier,
        name: String,
        targetMuscle: MuscleGroup,
        description: String,
        videoTutorial: String
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAll().first())
            exerciseDao.update(id, validName, description, targetMuscle, videoTutorial)
        }
    }


    suspend fun addExercise(
        id: ItemIdentifier,
        name: String,
        targetMuscle: MuscleGroup,
        description: String,
        videoTutorial: String,
    ): Exercise {
        return withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseDao.getAll().first())

            try {
                val entry = Exercise(id, validName, description, targetMuscle, videoTutorial)
                exerciseDao.insert(entry)
                return@withContext entry
            } catch (e: SQLiteConstraintException) {
                //Log.e(TAG, "Exercise with id: $id already exists!")
                throw e
            }
        }
    }


    suspend fun removeExercise(id: ItemIdentifier): Boolean {
       return exerciseDao.delete(id) > 0
    }


    suspend fun getMaxId(): ItemIdentifier = exerciseDao.getMaxId()
}