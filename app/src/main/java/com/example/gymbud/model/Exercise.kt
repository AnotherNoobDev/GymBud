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

data class ExerciseContent(
    override var name: String,
    var description: String,
    var targetMuscle: MuscleGroup,
    var resistance: ResistanceType,
): ItemContent


// A more specific description of the exercise to perform,
// including things like target rep range
data class ExerciseTemplate(
    override val id: ItemIdentifier,
    override var name: String,
    val exercise: Exercise,
    var targetRepRange: IntRange
    //todo: targetRestPeriod
): Item {
}

open class ExerciseTemplateEditContent(
    override var name: String,
    var targetRepRange: IntRange
): ItemContent {
}

class ExerciseTemplateNewContent(
    name: String,
    var exercise: Exercise,
    targetRepRange: IntRange
): ExerciseTemplateEditContent(name, targetRepRange) {
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
