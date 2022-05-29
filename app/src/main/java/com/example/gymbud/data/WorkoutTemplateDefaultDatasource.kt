package com.example.gymbud.data

import com.example.gymbud.model.*


const val WORKOUT_CHEST_BACK_SHOULDERS_1 = "Workout Chest, Back and Shoulders 1"
const val WORKOUT_CHEST_BACK_SHOULDERS_2 = "Workout Chest, Back and Shoulders 2"

const val WORKOUT_LEGS_ARMS_TRAPS_NECK_1 = "Workout Legs, Arms, Traps and Neck 1"
const val WORKOUT_LEGS_ARMS_TRAPS_NECK_2 = "Workout Legs, Arms, Traps and Neck 2"



object WorkoutTemplateDefaultDatasource {
    val workoutTemplatesForHyperTrophy: List<WorkoutTemplate>

    init {
        workoutTemplatesForHyperTrophy = generateDefaultWorkoutTemplatesForHypertrophy()
    }


    private fun generateDefaultWorkoutTemplatesForHypertrophy(): List<WorkoutTemplate> {
        val chestBack1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_1)!!
        val chestBack2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_2)!!
        val shoulders1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(SHOULDERS_SET_1)!!

        val legs1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_SET_1)!!
        val legsArmsTraps1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_ARMS_TRAPS_SET_1)!!
        val calvesNeck1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CALVES_NECK_SET_1)!!

        val chestBack3 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_3)!!
        val chestBack4 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_4)!!
        val shoulders2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(SHOULDERS_SET_2)!!

        val legsArmsTraps2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_ARMS_TRAPS_SET_2)!!
        val calvesNeck2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CALVES_NECK_SET_2)!!

        val rest60to120 = RestPeriodDefaultDatasource.rest60to120
        val rest180to300 = RestPeriodDefaultDatasource.rest180to300

        return listOf(
            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_CHEST_BACK_SHOULDERS_1)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(shoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,
            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_LEGS_ARMS_TRAPS_NECK_1)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legsArmsTraps1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legsArmsTraps1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legsArmsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,
            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_CHEST_BACK_SHOULDERS_2)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(chestBack4, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBack4, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack4, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack4, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shoulders2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(shoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,
            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_LEGS_ARMS_TRAPS_NECK_2)
                .add(TaggedItem.makeTagged(legsArmsTraps2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legsArmsTraps2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(legsArmsTraps2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsTraps2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsTraps2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(calvesNeck2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(calvesNeck2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck2, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate
        )
    }


    fun getWorkoutTemplateForHypertrophyByName(name: String): WorkoutTemplate? {
        return workoutTemplatesForHyperTrophy.find { it.name.contains(name) }
    }
}