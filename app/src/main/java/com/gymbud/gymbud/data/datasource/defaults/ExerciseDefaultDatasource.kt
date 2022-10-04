package com.gymbud.gymbud.data.datasource.defaults

import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.model.Exercise
import com.gymbud.gymbud.model.ExerciseTemplate
import com.gymbud.gymbud.model.MuscleGroup

// CHEST
const val BENCH_PRESS =  "Bench Press"
const val PUSH_UPS = "Push-ups"
const val INCLINE_PRESS = "Incline Press"
const val CHEST_FLY = "Chest Fly"

// BACK
const val PULL_UPS = "Pull-ups"
const val ROWS = "Rows"
const val BARBELL_SHRUG = "Barbell Shrug"
const val DUMBELL_SHRUG = "Dumbbell Shrug"
const val DEADLIFT = "Deadlift"
const val INVERTED_ROW = "Inverted Row"

// SHOULDERS
const val OHP = "Overhead Press"
const val ARNOLD_PRESS = "Arnold Press"
const val LATERAL_RAISES = "Lateral Raises"
const val REVERSE_FLY = "Reverse Fly"
const val FACE_PULL = "Face Pull"
const val INCLINE_CURL = "Incline Curl"

// ARMS
const val BICEPS_CURL = "Biceps Curl"
const val SKULLCRUSHERS = "Skull Crushers"
const val PUSH_DOWN = "Push-down"
const val REVERSE_CURL = "Reverse Curl"
const val HAMMER_CURL = "Hammer Curl"
const val SUPINATED_CURL = "Supinated Curl"
const val REVERSE_PUSH_DOWN = "Reverse Push-down"

// NECK
const val NECK_CURL = "Neck Curl"
const val NECK_EXTENSIONS = "Neck Extensions"

// LEGS
const val SQUAT = "Squat"
const val NORDIC_CURL = "Nordic Curl"
const val HIP_THRUST = "Hip Thrust"
const val LUNGES = "Lunges"

// CALVES
const val CALF_RAISES = "Calf Raises"


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
                "", //TODO take notes from video (same for the others bellow)
                MuscleGroup.CHEST,
                "vcBig73ojpE"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                ROWS,
                "",
                MuscleGroup.BACK,
                "T3N-TO4reLQ"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PUSH_UPS,
                "",
                MuscleGroup.CHEST,
                "IODxDxX7oi4"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PULL_UPS,
                "",
                MuscleGroup.BACK,
                "B5z3k20QCS0"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                OHP,
                "",
                MuscleGroup.FRONT_DELT,
                "hxH2F2Qp2YA"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                LATERAL_RAISES,
                "",
                MuscleGroup.SIDE_DELT,
                "fD6kaKjiy84"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                REVERSE_FLY,
                "",
                MuscleGroup.REAR_DELT,
                "lPt0GqwaqEw"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                SQUAT,
                "",
                MuscleGroup.QUADS,
                "Uv_DKDl7EjA"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                BARBELL_SHRUG,
                "",
                MuscleGroup.BACK,
                "Jl2fzN3mY_k"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                DUMBELL_SHRUG,
                "",
                MuscleGroup.BACK,
                "Jl2fzN3mY_k"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                HIP_THRUST,
                "",
                MuscleGroup.GLUTES,
                "xDmFkJxPzeM"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INCLINE_CURL,
                "",
                MuscleGroup.BICEPS,
                "soxrZlIl35U"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                PUSH_DOWN,
                "",
                MuscleGroup.TRICEPS,
                "GCa8Q4e7laU"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                CALF_RAISES,
                "",
                MuscleGroup.CALVES,
                "Xa18jxyeSnM"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INCLINE_PRESS,
                "",
                MuscleGroup.CHEST,
                "GHcAIiL9J_Y"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NECK_EXTENSIONS,
                "",
                MuscleGroup.NECK,
                "qQz2JoBn19k"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                CHEST_FLY,
                "",
                MuscleGroup.CHEST,
                "2dKBS61BX24"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                INVERTED_ROW,
                "",
                MuscleGroup.BACK,
                "s1A4i8dQeu8"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                ARNOLD_PRESS,
                "",
                MuscleGroup.FRONT_DELT,
                "6Z15_WdXmVw"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                FACE_PULL,
                "",
                MuscleGroup.REAR_DELT,
                "eIq5CB9JfKE"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NORDIC_CURL,
                "",
                MuscleGroup.BACK,
                "F-AaE8mw_pY"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                LUNGES,
                "",
                MuscleGroup.QUADS,
                "0MZd3iKzzPM"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                BICEPS_CURL,
                "",
                MuscleGroup.BICEPS,
                "QZEqB6wUPxQ"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                SKULLCRUSHERS,
                "",
                MuscleGroup.TRICEPS,
                "ir5PsbniVSc"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                NECK_CURL,
                "",
                MuscleGroup.NECK,
                "ym8iHuzAMiU"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                DEADLIFT,
                "",
                MuscleGroup.BACK,
                "wYREQkVtvEc"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                REVERSE_PUSH_DOWN,
                "",
                MuscleGroup.TRICEPS,
                "xQO6-Yy_uKM"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                SUPINATED_CURL,
                "",
                MuscleGroup.BICEPS,
                "OtFLz4RwYMQ"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                REVERSE_CURL,
                "",
                MuscleGroup.BICEPS,
                "hfNRgBNVhk4"
            ),
            Exercise(
                ItemIdentifierGenerator.generateId(),
                HAMMER_CURL,
                "",
                MuscleGroup.BICEPS,
                "zC3nLlEvin4"
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
            PUSH_UPS -> 15..60
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
            ARNOLD_PRESS -> 10..15
            FACE_PULL -> 20..30
            NORDIC_CURL -> 3..12
            LUNGES -> 16..24
            BICEPS_CURL -> 8..12
            SKULLCRUSHERS -> 8..12
            NECK_CURL -> 20..30
            DEADLIFT -> 8..10
            REVERSE_PUSH_DOWN -> 16..24
            REVERSE_CURL -> 10..15
            HAMMER_CURL -> 10..15
            SUPINATED_CURL -> 10..15
            else -> 8..12
        }
    }


    fun getExerciseTemplateForHypertrophyByName(name: String): ExerciseTemplate? {
        return exerciseTemplatesForHypertrophy.find { it.name == (name + " " + HYPERTROPHY_NAME_TAG)}
    }
}
