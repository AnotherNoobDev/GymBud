package com.example.gymbud.data.datasource.defaults

import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.model.ProgramTemplate
import com.example.gymbud.model.RestPeriod


const val DEFAULT_HYPERTROPHY_PROGRAM = "4 day-split Hypertrophy Program"


object ProgramTemplateDefaultDatasource {
    val programs: List<ProgramTemplate>

    init {
        val programForHypertrophy = ProgramTemplate(ItemIdentifierGenerator.generateId(), DEFAULT_HYPERTROPHY_PROGRAM)
            .add(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_CHEST_BACK_SHOULDERS_1)!!)
            .add(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_LEGS_ARMS_TRAPS_NECK_1)!!)
            .add(RestPeriod.RestDay)
            .add(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_CHEST_BACK_SHOULDERS_2)!!)
            .add(WorkoutTemplateDefaultDatasource.getWorkoutTemplateForHypertrophyByName(WORKOUT_LEGS_ARMS_TRAPS_NECK_2)!!)
            .add(RestPeriod.RestDay)
            .add(RestPeriod.RestDay)

        programs = listOf(programForHypertrophy as ProgramTemplate)
    }
}