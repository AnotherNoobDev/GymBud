package com.example.gymbud.model

// A description of the exercise
data class Exercise(
    override val id: ItemIdentifier,
    override var name: String,
    var description: String,
    var targetMuscle: MuscleGroup,
    var resistance: ResistanceType
) : Item {

    override fun toString(): String {
        return name
    }
}


// A more specific description of the exercise to perform,
// including things like target rep range
data class ExerciseTemplate(
    override val id: ItemIdentifier,
    override var name: String,
    val exercise: Exercise,
    var targetRepRange: IntRange
    //todo: targetRestPeriod
): BasicBlock, Item {
}


// An actual executed exercise
data class ExerciseRecord(
    override val id: ItemIdentifier,
    override var name: String,
    val exerciseTemplate: ExerciseTemplate,
    val targetReps: Int,
    val actualReps: Int,
    val targetResistance: String, // weight -> number, band-> string todo: how to better represent this?
    val actualResistance: String
    // val period -> time it took to perform the exercise
): Item {
}
