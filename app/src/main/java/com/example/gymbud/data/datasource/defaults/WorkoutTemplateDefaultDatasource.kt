package com.example.gymbud.data.datasource.defaults

import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.model.*


const val WORKOUT_1 = "Chest, Back, Shoulders and Neck"
const val WORKOUT_2 = "Legs, Arms and Shoulders"

const val WORKOUT_3 = "Chest, Back and Shoulders"
const val WORKOUT_4 = "Arms and Shoulders"
const val WORKOUT_5 = "Legs, Traps and Neck"



object WorkoutTemplateDefaultDatasource {
    val workoutTemplatesForHyperTrophy: List<WorkoutTemplate>

    init {
        workoutTemplatesForHyperTrophy = generateDefaultWorkoutTemplatesForHypertrophy()
    }


    private fun generateDefaultWorkoutTemplatesForHypertrophy(): List<WorkoutTemplate> {
        val chestBack1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_1)!!
        val shouldersBack1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(SHOULDERS_BACK_SET_1)!!
        val chestBackNeck1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_NECK_SET_1)!!

        val legsArms1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_ARMS_SET_1)!!
        val legsArmsShoulders1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_ARMS_SHOULDERS_SET_1)!!
        val calvesShoulders1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CALVES_SHOULDERS_SET_1)!!

        val deadlift1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(BACK_SET_1)!!
        val chestBack2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_2)!!
        val chestBack3 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CHEST_BACK_SET_3)!!
        val shouldersFinisher = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(SHOULDERS_FINISHER_SET)!!

        val armsShoulders1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(ARMS_SHOULDERS_SET_1)!!
        val armsShoulders2 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(ARMS_SHOULDERS_SET_2)!!
        val armsFinisher = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(ARMS_FINISHER_SET)!!

        val legs1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_SET_1)!!
        val legsTraps1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(LEGS_TRAPS_SET_1)!!
        val calvesNeck1 = SetTemplateDefaultDatasource.getSetTemplateForHypertrophyByName(CALVES_NECK_SET_1)!!

        val rest60 = RestPeriodDefaultDatasource.rest60
        val rest60to120 = RestPeriodDefaultDatasource.rest60to120
        val rest180to300 = RestPeriodDefaultDatasource.rest180to300

        return listOf(
            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_1)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shouldersBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(shouldersBack1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(shouldersBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shouldersBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(shouldersBack1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(chestBackNeck1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(TaggedItem.makeTagged(chestBackNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBackNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBackNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,

            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_2)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArms1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsArmsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(calvesShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(calvesShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,

            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_3)
                .add(TaggedItem.makeTagged(deadlift1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(deadlift1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(deadlift1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(deadlift1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(deadlift1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(chestBack3, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shouldersFinisher, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(shouldersFinisher, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shouldersFinisher, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(shouldersFinisher, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,

            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_4)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(armsShoulders1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(armsShoulders2, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(armsShoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(armsShoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(armsShoulders2, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(armsFinisher, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate,

            WorkoutTemplate(ItemIdentifierGenerator.generateId(), WORKOUT_5)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legs1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(legsTraps1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsTraps1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(legsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(legsTraps1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest180to300)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Warmup.toString()))
                .add(rest60)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                .add(rest60to120)
                .add(TaggedItem.makeTagged(calvesNeck1, TagCategory.Intensity, SetIntensity.Working.toString()))
                    as WorkoutTemplate
        )
    }


    fun getWorkoutTemplateForHypertrophyByName(name: String): WorkoutTemplate? {
        return workoutTemplatesForHyperTrophy.find { it.name == name }
    }
}