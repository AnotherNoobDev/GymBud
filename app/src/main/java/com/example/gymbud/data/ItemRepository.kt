package com.example.gymbud.data

import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ItemRepository(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val programRepository: ProgramRepository
) {
    fun getItemsByType(type: ItemType): Flow<List<Item>> {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.exercises
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.exerciseTemplates
            ItemType.SET_TEMPLATE-> setTemplateRepository.setTemplates
            ItemType.WORKOUT_TEMPLATE-> workoutTemplateRepository.workoutTemplates
            ItemType.PROGRAM -> programRepository.programs
        }
    }


    fun getItem(id: ItemIdentifier, type: ItemType? = null): Item? {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.retrieveExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.retrieveExerciseTemplate(id)
            else -> findItemInAll(id)
        }
    }


    private fun findItemInAll(id: ItemIdentifier): Item? {
        var item: Item?

        item = exerciseRepository.retrieveExercise(id)
        if (item != null) return item

        item = exerciseTemplateRepository.retrieveExerciseTemplate(id)
        if (item != null) return item

        /* todo */


        return item
    }


    fun addItem(tempItem: Item) {
        if (tempItem is Exercise) {
            addExercise(tempItem)
        } else if (tempItem is ExerciseTemplate) {
            addExerciseTemplate(tempItem)
        }

        // todo
    }


    private fun addExercise(tempExercise: Exercise) {
        exerciseRepository.addExercise(
            ItemIdentifierGenerator.generateId(),
            tempExercise.name,
            tempExercise.resistance,
            tempExercise.targetMuscle,
            tempExercise.description
        )
    }


    private fun addExerciseTemplate(tempExerciseTemplate: ExerciseTemplate) {
        exerciseTemplateRepository.addExerciseTemplate(
            ItemIdentifierGenerator.generateId(),
            tempExerciseTemplate.name,
            tempExerciseTemplate.exercise,
            tempExerciseTemplate.targetRepRange
        )
    }


    fun updateItem(id: ItemIdentifier, tempItem: Item) {
        if (tempItem is Exercise) {
            updateExercise(id, tempItem)
        } else if (tempItem is ExerciseTemplate) {
            updateExerciseTemplate(id, tempItem)
        }

        // todo
    }


    private fun updateExercise(id: ItemIdentifier, tempExercise: Exercise) {
        exerciseRepository.updateExercise(
            id,
            tempExercise.name,
            tempExercise.resistance,
            tempExercise.targetMuscle,
            tempExercise.description
        )
    }


    private fun updateExerciseTemplate(id: ItemIdentifier, tempExerciseTemplate: ExerciseTemplate) {
        exerciseTemplateRepository.updateExerciseTemplate(
            id,
            tempExerciseTemplate.name,
            tempExerciseTemplate.targetRepRange
        )
    }


    fun removeItem(id: ItemIdentifier, type: ItemType? = null) {
        when (type) {
            ItemType.EXERCISE -> exerciseRepository.removeExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.removeExerciseTemplate(id)
            else -> removeItemInAll(id)
        }

        // todo
    }


    private fun removeItemInAll(id: ItemIdentifier) {
        exerciseRepository.removeExercise(id) ||
                exerciseTemplateRepository.removeExerciseTemplate(id)

        // todo
    }
}