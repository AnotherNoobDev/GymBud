package com.example.gymbud.data

import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow

class ItemRepository(
    private val exerciseRepository: ExerciseRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val restPeriodRepository: RestPeriodRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val programRepository: ProgramRepository
) {
    fun getItemsByType(type: ItemType): Flow<List<Item>> {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.exercises
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.exerciseTemplates
            ItemType.REST_PERIOD -> restPeriodRepository.restPeriods
            ItemType.SET_TEMPLATE-> setTemplateRepository.setTemplates
            ItemType.WORKOUT_TEMPLATE-> workoutTemplateRepository.workoutTemplates
            ItemType.PROGRAM -> programRepository.programs
        }
    }


    fun getItem(id: ItemIdentifier, type: ItemType? = null): Item? {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.retrieveExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.retrieveExerciseTemplate(id)
            ItemType.SET_TEMPLATE -> setTemplateRepository.retrieveSetTemplate(id)
            ItemType.WORKOUT_TEMPLATE -> workoutTemplateRepository.retrieveWorkoutTemplate(id)
            else -> findItemInAll(id)
        }

        // todo
    }


    private fun findItemInAll(id: ItemIdentifier): Item? {
        var item: Item?

        item = exerciseRepository.retrieveExercise(id)
        if (item != null) return item

        item = exerciseTemplateRepository.retrieveExerciseTemplate(id)
        if (item != null) return item

        item = setTemplateRepository.retrieveSetTemplate(id)
        if (item != null) return item

        item = workoutTemplateRepository.retrieveWorkoutTemplate(id)
        if (item != null) return item

        // todo

        return item
    }


    fun addItem(content: ItemContent) {
        when (content) {
            is ExerciseContent -> {
                addExercise(content)
            }
            is ExerciseTemplateNewContent -> {
                addExerciseTemplate(content)
            }
            is SetTemplateContent -> {
                addSetTemplate(content)
            }
            is WorkoutTemplateContent -> {
                addWorkoutTemplate(content)
            }
        }

        // todo
    }


    private fun addExercise(content: ExerciseContent) {
        exerciseRepository.addExercise(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.resistance,
            content.targetMuscle,
            content.description
        )
    }


    private fun addExerciseTemplate(content: ExerciseTemplateNewContent) {
        exerciseTemplateRepository.addExerciseTemplate(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.exercise,
            content.targetRepRange
        )
    }

    private fun addSetTemplate(content: SetTemplateContent) {
        setTemplateRepository.addSetTemplate(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.items
        )
    }

    private fun addWorkoutTemplate(content: WorkoutTemplateContent) {
        workoutTemplateRepository.addWorkoutTemplate(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.items
        )
    }


    fun updateItem(id: ItemIdentifier, content: ItemContent) {
        when (content) {
            is ExerciseContent -> {
                updateExercise(id, content)
            }
            is ExerciseTemplateEditContent -> {
                updateExerciseTemplate(id, content)
            }
            is SetTemplateContent -> {
                updateSetTemplate(id, content)
            }
            is WorkoutTemplateContent -> {
                updateWorkoutTemplate(id, content)
            }
        }

        // todo
    }


    private fun updateExercise(id: ItemIdentifier, content: ExerciseContent) {
        exerciseRepository.updateExercise(
            id,
            content.name,
            content.resistance,
            content.targetMuscle,
            content.description
        )
    }


    private fun updateExerciseTemplate(id: ItemIdentifier, content: ExerciseTemplateEditContent) {
        exerciseTemplateRepository.updateExerciseTemplate(
            id,
            content.name,
            content.targetRepRange
        )
    }

    private fun updateSetTemplate(id: ItemIdentifier, content: SetTemplateContent) {
        setTemplateRepository.updateSetTemplate(
            id,
            content.name,
            content.items
        )
    }

    private fun updateWorkoutTemplate(id: ItemIdentifier, content: WorkoutTemplateContent) {
        workoutTemplateRepository.updateWorkoutTemplate(
            id,
            content.name,
            content.items
        )
    }


    fun removeItem(id: ItemIdentifier, type: ItemType? = null) {
        when (type) {
            ItemType.EXERCISE -> exerciseRepository.removeExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.removeExerciseTemplate(id)
            ItemType.SET_TEMPLATE -> setTemplateRepository.removeSetTemplate(id)
            ItemType.WORKOUT_TEMPLATE -> workoutTemplateRepository.removeWorkoutTemplate(id)
            else -> removeItemInAll(id)
        }

        // todo
    }


    private fun removeItemInAll(id: ItemIdentifier) {
        exerciseRepository.removeExercise(id) ||
                exerciseTemplateRepository.removeExerciseTemplate(id) ||
                setTemplateRepository.removeSetTemplate(id) ||
                workoutTemplateRepository.removeWorkoutTemplate(id)

        // todo
    }
}