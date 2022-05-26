package com.example.gymbud.data

import com.example.gymbud.model.ItemContainer
import com.example.gymbud.model.SetTemplate


const val CHEST_BACK_SET_1 = "Chest and Back 1"
const val CHEST_BACK_SET_2 = "Chest and Back 2"
const val CHEST_BACK_SET_3 = "Chest and Back 3"
const val CHEST_BACK_SET_4 = "Chest and Back 4"

const val SHOULDERS_SET_1 = "Shoulders 1"
const val SHOULDERS_SET_2 = "Shoulders 1"

const val LEGS_SET_1 = "Squats"

const val LEGS_ARMS_TRAPS_SET_1 = "Legs, arms and traps 1"
const val LEGS_ARMS_TRAPS_SET_2 = "Legs, arms and traps 2"

const val CALVES_NECK_SET_1 = "Calves and Neck 1"
const val CALVES_NECK_SET_2 = "Calves and Neck 1"



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

        val rest60 = RestPeriodDefaultDatasource.rest60
        val rest60to120 = RestPeriodDefaultDatasource.rest60to120

        // todo add rest blocks
        return listOf(
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_1
            ).add(bench).add(rest60).add(rows) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_2
            ).add(pushUps).add(rest60to120).add(pullUps) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_3
            ).add(inclinePress).add(rest60).add(pullUps) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_4
            ).add(chestFly).add(rest60).add(invertedRow) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_SET_1
            ).add(ohp).add(lateralRaises).add(reverseFly) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_SET_2
            ).add(arnoldPress).add(lateralRaises).add(facePull) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_SET_1
            ).add(squat) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_TRAPS_SET_1
            ).add(dbShrug).add(pushDown).add(inclineCurl).add(hipThrust) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_TRAPS_SET_2
            ).add(barbellShrug).add(lunges).add(bicepsCurl).add(nordicCurl).add(skullcrushers) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_NECK_SET_1
            ).add(calfRaises).add(neckExtensions) as SetTemplate,
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_NECK_SET_2
            ).add(calfRaises).add(neckCurl) as SetTemplate
        )
    }


    fun getSetTemplateForHypertrophyByName(name: String): SetTemplate? {
        return setTemplatesForHypertrophy.find { it.name.contains(name) }
    }
}

