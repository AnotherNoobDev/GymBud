package com.gymbud.gymbud.data.datasource.defaults

import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.model.SetTemplate


// Workout 1 - Chest, Back, Shoulders and Neck
const val CHEST_BACK_SET_1 = "Chest and Back 1"
const val SHOULDERS_BACK_SET_1 = "Shoulders and Back"
const val CHEST_BACK_NECK_SET_1 = "Chest, Traps and Neck"


// Workout 2 - Legs, Arms and Shoulders
const val LEGS_ARMS_SET_1 = "Legs and Arms 1"
const val LEGS_ARMS_SHOULDERS_SET_1 = "Legs, Arms and Shoulders"
const val CALVES_SHOULDERS_SET_1 = "Calves and Shoulders"

// Workout 3 - Chest, Back and Shoulders
const val BACK_SET_1 = "Deadlifts"
const val CHEST_BACK_SET_2 = "Chest and Back 2"
const val CHEST_BACK_SET_3 = "Chest and Back 3"
const val SHOULDERS_FINISHER_SET = "Shoulders Finisher Set"

// Workout 4 - Arms and Shoulders
const val ARMS_SHOULDERS_SET_1 = "Arms and Shoulders 1"
const val ARMS_SHOULDERS_SET_2 = "Arms and Shoulders 2"
const val ARMS_FINISHER_SET = "Arms Finisher"

// Workout 5 - Legs, Traps and Neck
const val LEGS_SET_1 = "Squats"
const val LEGS_TRAPS_SET_1 = "Legs and Traps"
const val CALVES_NECK_SET_1 = "Calves and Neck"


object SetTemplateDefaultDatasource {
    val setTemplatesForHypertrophy:  List<SetTemplate>

    init {
        setTemplatesForHypertrophy = generateDefaultSetTemplatesForHypertrophy()
    }

    private fun generateDefaultSetTemplatesForHypertrophy(): List<SetTemplate> {
        val bench = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(BENCH_PRESS)!!
        val rows = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(ROWS)!!
        val pushUps = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(PUSH_UPS)!!
        val pullUps = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(PULL_UPS)!!
        val ohp = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(OHP)!!
        val lateralRaises = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(LATERAL_RAISES)!!
        val reverseFly = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(REVERSE_FLY)!!
        val squat = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(SQUAT)!!
        val barbellShrug = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(BARBELL_SHRUG)!!
        val dbShrug = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(DUMBELL_SHRUG)!!
        val hipThrust = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(HIP_THRUST)!!
        val inclineCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(INCLINE_CURL)!!
        val pushDown = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(PUSH_DOWN)!!
        val calfRaises = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(CALF_RAISES)!!
        val inclinePress = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(INCLINE_PRESS)!!
        val neckExtensions = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(NECK_EXTENSIONS)!!
        val chestFly = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(CHEST_FLY)!!
        val invertedRow = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(INVERTED_ROW)!!
        val arnoldPress = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(ARNOLD_PRESS)!!
        val facePull = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(FACE_PULL)!!
        val nordicCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(NORDIC_CURL)!!
        val lunges = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(LUNGES)!!
        val bicepsCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(BICEPS_CURL)!!
        val skullcrushers = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(SKULLCRUSHERS)!!
        val neckCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(NECK_CURL)!!
        val deadlift = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(DEADLIFT)!!
        val reversePushDown = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(REVERSE_PUSH_DOWN)!!
        val reverseCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(REVERSE_CURL)!!
        val hammerCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(HAMMER_CURL)!!
        val supinatedCurl = ExerciseDefaultDatasource.getExerciseTemplateForHypertrophyByName(SUPINATED_CURL)!!

        val rest60 = RestPeriodDefaultDatasource.rest60

        return listOf(
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_1
            ).add(bench).add(rest60).add(pullUps) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_BACK_SET_1
            ).add(ohp).add(rest60).add(rows) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_NECK_SET_1
            ).add(pushUps).add(rest60).add(dbShrug).add(rest60).add(neckCurl) as SetTemplate,


            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_SET_1
            ).add(bicepsCurl).add(rest60).add(hipThrust) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_SHOULDERS_SET_1
            ).add(skullcrushers).add(rest60).add(lunges).add(rest60).add(lateralRaises) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_SHOULDERS_SET_1
            ).add(calfRaises).add(reverseFly) as SetTemplate,


            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                BACK_SET_1
            ).add(deadlift) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_2
            ).add(inclinePress).add(rest60).add(pullUps) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_3
            ).add(chestFly).add(rest60).add(invertedRow) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_FINISHER_SET
            ).add(arnoldPress) as SetTemplate,


            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                ARMS_SHOULDERS_SET_1
            ).add(bicepsCurl).add(rest60).add(skullcrushers).add(rest60).add(facePull) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                ARMS_SHOULDERS_SET_2
            ).add(inclineCurl).add(rest60).add(pushDown).add(rest60).add(lateralRaises) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                ARMS_FINISHER_SET
            ).add(reverseCurl).add(reversePushDown).add(hammerCurl).add(reversePushDown).add(supinatedCurl).add(reversePushDown) as SetTemplate,


            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_SET_1
            ).add(squat) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_TRAPS_SET_1
            ).add(barbellShrug).add(rest60).add(nordicCurl) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_NECK_SET_1
            ).add(neckExtensions).add(rest60).add(calfRaises) as SetTemplate
        )
    }


    fun getSetTemplateForHypertrophyByName(name: String): SetTemplate? {
        return setTemplatesForHypertrophy.find { it.name == name }
    }
}

