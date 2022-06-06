package com.example.gymbud.data

import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ExerciseTemplate
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType

const val BENCH_PRESS =  "Bench press"
const val ROWS = "Rows"
const val PUSH_UPS = "Push-ups"
const val PULL_UPS = "Pull-ups"
const val OHP = "OHP"
const val LATERAL_RAISES = "Lateral raises"
const val REVERSE_FLY = "Reverse fly"
const val SQUAT = "Squat"
const val BARBELL_SHRUG = "Barbell Shrug"
const val DUMBELL_SHRUG = "Dumbbell Shrug"
const val HIP_THRUST = "Hip thrust"
const val INCLINE_CURL = "Incline curl"
const val PUSH_DOWN = "Push-down"
const val CALF_RAISES = "Calf raises"
const val INCLINE_PRESS = "Incline press"
const val NECK_EXTENSIONS = "Neck extensions"
const val CHEST_FLY = "Chest fly"
const val INVERTED_ROW = "Inverted row"
const val ARNOLD_PRESS = "Arnold press"
const val FACE_PULL = "Face pull"
const val NORDIC_CURL = "Nordic curl"
const val LUNGES = "Lunges"
const val BICEPS_CURL = "Biceps curl"
const val SKULLCRUSHERS = "Skullcrushers"
const val NECK_CURL = "Neck curl"


const val HYPERTROPHY_NAME_TAG = "Hypertrophy"


object ExerciseDefaultDatasource {
    val exercises: List<Exercise> by lazy { generateDefaultExercises() }
    val exerciseTemplatesForHypertrophy: List<ExerciseTemplate> by lazy {
        generateDefaultExerciseTemplatesForHypertrophy(exercises)
    }


    private fun generateDefaultExercises(): List<Exercise> {
        return listOf(
            Exercise(
                ItemIdentifierGenerator.generateId(),
                BENCH_PRESS,
                "Add notes here...\nAdd notes here...\nAdd notes here...\nAdd notes here...\nAdd notes here...\n",
                MuscleGroup.CHEST,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                ROWS,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PUSH_UPS,
                "Add notes here...",
                MuscleGroup.CHEST,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PULL_UPS,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                OHP,
                "Add notes here...",
                MuscleGroup.FRONT_DELT,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                LATERAL_RAISES,
                "Add notes here...",
                MuscleGroup.SIDE_DELT,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                REVERSE_FLY,
                "Add notes here...",
                MuscleGroup.REAR_DELT,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                SQUAT,
                "Add notes here...",
                MuscleGroup.QUADS,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                BARBELL_SHRUG,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                DUMBELL_SHRUG,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                HIP_THRUST,
                "Add notes here...",
                MuscleGroup.GLUTES,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INCLINE_CURL,
                "Add notes here...",
                MuscleGroup.BICEPS,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PUSH_DOWN,
                "Add notes here...",
                MuscleGroup.TRICEPS,
                ResistanceType.BAND
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                CALF_RAISES,
                "Add notes here...",
                MuscleGroup.CALVES,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INCLINE_PRESS,
                "Add notes here...",
                MuscleGroup.CHEST,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NECK_EXTENSIONS,
                "Add notes here...",
                MuscleGroup.NECK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                CHEST_FLY,
                "Add notes here...",
                MuscleGroup.CHEST,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INVERTED_ROW,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                ARNOLD_PRESS,
                "Add notes here...",
                MuscleGroup.FRONT_DELT,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                FACE_PULL,
                "Add notes here...",
                MuscleGroup.REAR_DELT,
                ResistanceType.BAND
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NORDIC_CURL,
                "Add notes here...",
                MuscleGroup.BACK,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                LUNGES,
                "Add notes here...",
                MuscleGroup.QUADS,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                BICEPS_CURL,
                "Add notes here...",
                MuscleGroup.BICEPS,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                SKULLCRUSHERS,
                "Add notes here...",
                MuscleGroup.TRICEPS,
                ResistanceType.WEIGHT
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NECK_CURL,
                "Add notes here...",
                MuscleGroup.NECK,
                ResistanceType.WEIGHT
            )
        )
    }


    private fun generateDefaultExerciseTemplatesForHypertrophy(exercises: List<Exercise>): List<ExerciseTemplate> {
        return exercises.map { generateExerciseTemplateForHypetrophy(it) }
    }


    private fun generateExerciseTemplateForHypetrophy(exercise: Exercise): ExerciseTemplate {
        return ExerciseTemplate(
            ItemIdentifierGenerator.generateId(),
            exercise.name + " " + HYPERTROPHY_NAME_TAG,
            exercise,
            getRepRangeForHypertrophy(exercise)
        )
    }


    private fun getRepRangeForHypertrophy(exercise: Exercise) : IntRange {
        return when (exercise.name) {
            BENCH_PRESS -> 8..12
            ROWS -> 8..12
            PUSH_UPS -> 20..60
            PULL_UPS -> 3..15
            OHP -> 8..12
            LATERAL_RAISES -> 20..30
            REVERSE_FLY -> 20..30
            SQUAT -> 6..8
            DUMBELL_SHRUG -> 8..12
            BARBELL_SHRUG -> 8..12
            HIP_THRUST -> 8..12
            INCLINE_CURL -> 8..12
            PUSH_DOWN -> 16..24
            CALF_RAISES -> 20..30
            INCLINE_PRESS -> 8..12
            NECK_EXTENSIONS -> 20..30
            CHEST_FLY -> 8..12
            INVERTED_ROW -> 8..15
            ARNOLD_PRESS -> 8..12
            FACE_PULL -> 20..30
            NORDIC_CURL -> 3..12
            LUNGES -> 16..24
            BICEPS_CURL -> 8..12
            SKULLCRUSHERS -> 8..12
            NECK_CURL -> 20..30
            else -> 8..12
        }
    }


    fun getExerciseTemplateForHypertrophyByName(name: String): ExerciseTemplate? {
        return exerciseTemplatesForHypertrophy.find { it.name.contains(name) }
    }
}
