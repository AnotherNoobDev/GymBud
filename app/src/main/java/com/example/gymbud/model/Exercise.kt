package com.example.gymbud.model

data class Exercise(
    override val id: ItemIdentifier,
    override var name: String,
    var description: String,
    val targetMuscle: MuscleGroup,
    val resistance: ResistanceType
) : Item {

}