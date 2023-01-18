package com.gymbud.gymbud.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gymbud.gymbud.data.datasource.database.ExerciseTemplateDao
import com.gymbud.gymbud.data.datasource.defaults.ExerciseDefaultDatasource
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

private const val TAG = "ExerciseTemplateRepo"

class ExerciseTemplateRepository(
    private val exerciseTemplateDao: ExerciseTemplateDao,
    private val exerciseRepository: ExerciseRepository
) {
    val exerciseTemplates: Flow<List<ExerciseTemplate>> =
        exerciseTemplateDao.getAll().map { templates ->
            templates.forEach {
                exerciseRepository.fillExerciseContent(it.exercise)
            }

            return@map templates
        }


    val exerciseTemplatesByExercise = exerciseTemplates.map { templates ->
        exerciseRepository.exercises.first().mapNotNull { exercise ->
            val templatesByExercise = templates.filter { it.exercise.id == exercise.id }
            if (templatesByExercise.isEmpty()) {
                null
            } else {
                Pair(exercise, templatesByExercise)
            }
        }
    }


    suspend fun populateWithDefaults() {
        ExerciseDefaultDatasource.exerciseTemplatesForHypertrophy.forEach {
            exerciseTemplateDao.insert(it)
        }
    }


    fun retrieveExerciseTemplate(id: ItemIdentifier): Flow<ExerciseTemplate?> {
        return exerciseTemplateDao.get(id).map { exerciseTemplate ->
            if (exerciseTemplate != null) {
                // the exercise template will have only the id of the associated exercise after retrieving it from the DAO
                exerciseRepository.fillExerciseContent(exerciseTemplate.exercise)
            }

            return@map exerciseTemplate
        }
    }


    suspend fun retrieveExerciseTemplatesByExercise(exerciseId: ItemIdentifier): List<Item> {
        return exerciseTemplateDao.getByExercise(exerciseId)
    }


    suspend fun retrieveExerciseTemplates(ids: List<ItemIdentifier>): List<ExerciseTemplate> {
        val templates = exerciseTemplateDao.get(ids)
        templates.forEach {
            exerciseRepository.fillExerciseContent(it.exercise)
        }

        return templates
    }


    suspend fun hasExerciseTemplateWithSameContent(pendingEntry: ExerciseTemplate): ExerciseTemplate? {
        val repoEntry = exerciseTemplateDao.hasExerciseTemplateWithSameContent(pendingEntry.name, pendingEntry.exercise.id, pendingEntry.targetRepRange)?: return null

        exerciseRepository.fillExerciseContent(repoEntry.exercise)
        return repoEntry
    }



    suspend fun updateExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        targetRepRange: IntRange
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseTemplateDao.getAll().first())
            exerciseTemplateDao.update(id, validName, targetRepRange)
        }
    }


    suspend fun addExerciseTemplate(
        id: ItemIdentifier,
        name: String,
        exercise: Exercise,
        targetRepRange: IntRange
    ): ExerciseTemplate {
        return withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, exerciseTemplateDao.getAll().first())
            try {
                val entry = ExerciseTemplate(id, validName, exercise, targetRepRange)
                exerciseTemplateDao.insert(entry)
                return@withContext entry
            } catch (e: SQLiteConstraintException) {
                //Log.e(TAG, "Exercise template with id: $id already exists!")
                throw e
            }
        }
    }


    suspend fun removeExerciseTemplate(id: ItemIdentifier): Boolean {
        return exerciseTemplateDao.delete(id) > 0
    }
}