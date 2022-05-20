package com.example.gymbud.data

import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemType
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
}