package com.example.gymbud.data

import android.util.Log
import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier

private const val TAG = "ExerciseDetail"


class ExerciseRepository {
    private val _exercises: MutableList<Exercise> = mutableListOf()
    val exercises: List<Exercise>
        get() {
            return _exercises.toList()
        }

    init {
        _exercises.add(
            Exercise(
                ItemIdentifierGenerator.generateId(),
                "Biceps curl",
                "Go slow on the way down. Don't cheat",
                MuscleGroup.BICEPS,
                ResistanceType.WEIGHT)
        )

        _exercises.add(
            Exercise(
                ItemIdentifierGenerator.generateId(),
                "Skull crusher",
                "Arms around ears. Bring weight behind head on the way down. Don't lock out fully at the top.",
                MuscleGroup.TRICEPS,
                ResistanceType.WEIGHT)
        )

        _exercises.add(
            Exercise(
                ItemIdentifierGenerator.generateId(),
                "Squat",
                "Squeeze upper-back in position. Take deep breath. Pause at the bottom. Release breath at the top.",
                MuscleGroup.QUADS,
                ResistanceType.WEIGHT
            )
        )

        _exercises.add(
            Exercise(
                ItemIdentifierGenerator.generateId(),
                "Face pull",
                "On the floor.\n" +
                        "Pull hands to floor as if doing a double-biceps pose.\n" +
                        "Pause, then slowly return to starting pos.",
                MuscleGroup.REAR_DELT,
                ResistanceType.BAND
            )
        )
    }

    fun retrieveExercise(id: ItemIdentifier): Exercise? = _exercises.find{ it.id == id }

    fun updateExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        val exercise = retrieveExercise(id)
        exercise?.name = name
        exercise?.resistance = resistance
        exercise?.targetMuscle = targetMuscle
        exercise?.description = description
    }

    fun addExercise(
        id: ItemIdentifier,
        name: String,
        resistance: ResistanceType,
        targetMuscle: MuscleGroup,
        description: String
    ) {
        val exercise = retrieveExercise(id)
        if (exercise != null) {
            Log.e(TAG, "Exercise with id: " + id + "already exists!")
            // todo -> get a new id?
            return
        }

        _exercises.add(Exercise(id, name, description, targetMuscle, resistance))
    }
}