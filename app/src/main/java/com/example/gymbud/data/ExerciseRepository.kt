package com.example.gymbud.data

import com.example.gymbud.model.MuscleGroup
import com.example.gymbud.model.ResistanceType
import com.example.gymbud.model.Exercise


class ExerciseRepository {
    private val _exercises: MutableList<Exercise> = mutableListOf()
    val exercises: List<Exercise>
        get() {
            return _exercises.toList()
        }

    init {
        _exercises.add(
            Exercise(
                0,
                "Biceps curl",
                "Go slow on the way down. Don't cheat",
                MuscleGroup.BICEPS,
                ResistanceType.WEIGHT)
        )

        _exercises.add(
            Exercise(
                1,
                "Skull crusher",
                "Arms around ears. Bring weight behind head on the way down. Don't lock out fully at the top.",
                MuscleGroup.TRICEPS,
                ResistanceType.WEIGHT)
        )

        _exercises.add(
            Exercise(
                2,
                "Squat",
                "Squeeze upper-back in position. Take deep breath. Pause at the bottom. Release breath at the top.",
                MuscleGroup.QUADS,
                ResistanceType.WEIGHT
            )
        )

        _exercises.add(
            Exercise(
                3,
                "Face pull",
                "On the floor. Pull hands to floor as if doing a double-biceps pose. Pause, then slowly return to starting pos",
                MuscleGroup.REAR_DELT,
                ResistanceType.BAND
            )
        )
    }
}