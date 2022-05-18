package com.example.gymbud.data

import com.example.gymbud.model.Program
import com.example.gymbud.model.RestDay
import com.example.gymbud.model.WorkoutDay


const val DEFAULT_HYPERTROPHY_PROGRAM = "4 day-split Hypertrophy Program"


object ProgramDefaultDatasource {
    val programs: List<Program>

    init {
        val programForHypertrophy = Program(ItemIdentifierGenerator.generateId(), DEFAULT_HYPERTROPHY_PROGRAM)
            .addDay(WorkoutDay(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_CHEST_BACK_SHOULDERS_1)!!))
            .addDay(WorkoutDay(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_LEGS_ARMS_TRAPS_NECK_1)!!))
            .addDay(RestDay())
            .addDay(WorkoutDay(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_CHEST_BACK_SHOULDERS_2)!!))
            .addDay(WorkoutDay(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_LEGS_ARMS_TRAPS_NECK_2)!!))
            .addDay(RestDay())
            .addDay(RestDay())

        programs = listOf(programForHypertrophy)
    }
}