package com.example.gymbud.data

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

        // todo add rest blocks
        return listOf(
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_1
            ).addBlock(bench).addBlock(rows),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_2
            ).addBlock(pushUps).addBlock(pullUps),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_3
            ).addBlock(inclinePress).addBlock(pullUps),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CHEST_BACK_SET_4
            ).addBlock(chestFly).addBlock(invertedRow),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_SET_1
            ).addBlock(ohp).addBlock(lateralRaises).addBlock(reverseFly),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                SHOULDERS_SET_2
            ).addBlock(arnoldPress).addBlock(lateralRaises).addBlock(facePull),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_SET_1
            ).addBlock(squat),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_TRAPS_SET_1
            ).addBlock(dbShrug).addBlock(pushDown).addBlock(inclineCurl).addBlock(hipThrust),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                LEGS_ARMS_TRAPS_SET_2
            ).addBlock(barbellShrug).addBlock(lunges).addBlock(bicepsCurl).addBlock(nordicCurl).addBlock(skullcrushers),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_NECK_SET_1
            ).addBlock(calfRaises).addBlock(neckExtensions),
            SetTemplate(
                ItemIdentifierGenerator.generateId(),
                CALVES_NECK_SET_2
            ).addBlock(calfRaises).addBlock(neckCurl)
        )
    }


    fun getSetTemplateForHypertrophyByName(name: String): SetTemplate? {
        return setTemplatesForHypertrophy.find { it.name.contains(name) }
    }
}

