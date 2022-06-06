package com.example.gymbud.data

import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class ItemRepository(
    private val database: GymBudRoomDatabase,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val restPeriodRepository: RestPeriodRepository,
    private val setTemplateRepository: SetTemplateRepository,
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val programTemplateRepository: ProgramTemplateRepository
) {
    fun hasData(): Flow<Boolean> {
        // if there are no exercises, there can't be anything else
        return database.exerciseDao().count().map {
            it > 0
        }
    }


    suspend fun populateWithDefaults() {
        exerciseRepository.populateWithDefaults()
        exerciseTemplateRepository.populateWithDefaults()
        restPeriodRepository.populateWithDefaults()
        setTemplateRepository.populateWithDefaults()
    }


    fun purge() {
        database.clearAllTables()
    }


    fun getItemsByType(type: ItemType): Flow<List<Item>> {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.exercises
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.exerciseTemplates
            ItemType.REST_PERIOD -> restPeriodRepository.restPeriods
            ItemType.SET_TEMPLATE-> setTemplateRepository.setTemplates
            ItemType.WORKOUT_TEMPLATE-> workoutTemplateRepository.workoutTemplates
            ItemType.PROGRAM_TEMPLATE -> programTemplateRepository.programTemplates
            ItemType.UNKNOWN -> return flowOf()
        }
    }


    fun getItem(id: ItemIdentifier, type: ItemType? = null): Flow<Item?> {
        return when (type) {
            ItemType.EXERCISE -> exerciseRepository.retrieveExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.retrieveExerciseTemplate(id)
            ItemType.SET_TEMPLATE -> setTemplateRepository.retrieveSetTemplate(id)
            //ItemType.WORKOUT_TEMPLATE -> workoutTemplateRepository.retrieveWorkoutTemplate(id)
            //ItemType.PROGRAM_TEMPLATE -> programTemplateRepository.retrieveProgramTemplate(id)
            ItemType.REST_PERIOD -> restPeriodRepository.retrieveRestPeriod(id)
            //else -> findItemInAll(id)
            else -> exerciseRepository.retrieveExercise(id)

            // todo
        }
    }

    // todo
    /*
    private fun findItemInAll(id: ItemIdentifier): Flow<Item>  {
        var item: Item?

        item = exerciseRepository.retrieveExercise(id)
        if (item != null) return item

        item = exerciseTemplateRepository.retrieveExerciseTemplate(id)
        if (item != null) return item

        item = setTemplateRepository.retrieveSetTemplate(id)
        if (item != null) return item

        item = workoutTemplateRepository.retrieveWorkoutTemplate(id)
        if (item != null) return item

        item = programTemplateRepository.retrieveProgramTemplate(id)
        if (item != null) return item

        item = restPeriodRepository.retrieveRestPeriod(id)
        if (item != null) return item

        return item
    }

     */


    suspend fun addItem(content: ItemContent) {
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
            is ProgramTemplateContent -> {
                addProgramTemplate(content)
            }
        }

        // todo rest period?
    }


    private suspend fun addExercise(content: ExerciseContent) {
        exerciseRepository.addExercise(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.resistance,
            content.targetMuscle,
            content.description
        )
    }


    private suspend fun addExerciseTemplate(content: ExerciseTemplateNewContent) {
        exerciseTemplateRepository.addExerciseTemplate(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.exercise,
            content.targetRepRange
        )
    }

    private suspend fun addSetTemplate(content: SetTemplateContent) {
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

    private fun addProgramTemplate(content: ProgramTemplateContent) {
        programTemplateRepository.addProgramTemplate(
            ItemIdentifierGenerator.generateId(),
            content.name,
            content.items
        )
    }


    suspend fun updateItem(id: ItemIdentifier, content: ItemContent) {
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
            is ProgramTemplateContent -> {
                updateProgramTemplate(id, content)
            }
        }
    }


    private suspend fun updateExercise(id: ItemIdentifier, content: ExerciseContent) {
        exerciseRepository.updateExercise(
            id,
            content.name,
            content.resistance,
            content.targetMuscle,
            content.description
        )
    }


    private suspend fun updateExerciseTemplate(id: ItemIdentifier, content: ExerciseTemplateEditContent) {
        exerciseTemplateRepository.updateExerciseTemplate(
            id,
            content.name,
            content.targetRepRange
        )
    }

    private suspend fun updateSetTemplate(id: ItemIdentifier, content: SetTemplateContent) {
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

    private fun updateProgramTemplate(id: ItemIdentifier, content: ProgramTemplateContent) {
        programTemplateRepository.updateProgramTemplate(
            id,
            content.name,
            content.items
        )
    }


    suspend fun removeItem(id: ItemIdentifier, type: ItemType? = null) {
        when (type) {
            ItemType.EXERCISE -> exerciseRepository.removeExercise(id)
            ItemType.EXERCISE_TEMPLATE -> exerciseTemplateRepository.removeExerciseTemplate(id)
            ItemType.SET_TEMPLATE -> setTemplateRepository.removeSetTemplate(id)
            ItemType.WORKOUT_TEMPLATE -> workoutTemplateRepository.removeWorkoutTemplate(id)
            ItemType.PROGRAM_TEMPLATE -> programTemplateRepository.removeProgramTemplate(id)
            else -> removeItemInAll(id)
        }
    }


    // todo might want to replace with single query over all DB
    private suspend fun removeItemInAll(id: ItemIdentifier) {
        exerciseRepository.removeExercise(id) ||
                exerciseTemplateRepository.removeExerciseTemplate(id) ||
                setTemplateRepository.removeSetTemplate(id) ||
                workoutTemplateRepository.removeWorkoutTemplate(id) ||
                programTemplateRepository.removeProgramTemplate(id)

    }
}