package com.example.gymbud.model

import androidx.room.*


// A description of the exercise
@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String,
    var notes: String,
    @ColumnInfo(name = "target_muscle") var targetMuscle: MuscleGroup,
    var resistance: ResistanceType
) : Item {

    constructor(id: ItemIdentifier): this(id, "FILLER", "", MuscleGroup.BACK, ResistanceType.WEIGHT)

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
@Entity(
    tableName = "exercise_template",
    indices = [Index(value = ["exercise_id"])],
    foreignKeys = [ForeignKey(entity = Exercise::class, parentColumns = arrayOf("id"), childColumns = arrayOf("exercise_id"), onDelete = ForeignKey.CASCADE)]
)
data class ExerciseTemplate(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String,
    @ColumnInfo(name = "exercise_id") val exercise: Exercise,
    @ColumnInfo(name = "target_rep_range") var targetRepRange: IntRange
): Item


open class ExerciseTemplateEditContent(
    override var name: String,
    var targetRepRange: IntRange
): ItemContent


class ExerciseTemplateNewContent(
    name: String,
    var exercise: Exercise,
    targetRepRange: IntRange
): ExerciseTemplateEditContent(name, targetRepRange)
