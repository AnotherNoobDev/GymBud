package com.gymbud.gymbud.model

import androidx.room.*
import com.gymbud.gymbud.data.ItemIdentifierGenerator


enum class WorkoutSessionItemType {
    Exercise,
    Rest
}

open class WorkoutSessionItem private constructor(val type: WorkoutSessionItemType, val hint: String) {
    class ExerciseSession(
        private var id: ItemIdentifier,
        private val name: String,
        val exerciseTemplate: ExerciseTemplate,
        val tags: Tags?,
        private var previousSession: ExerciseSession?,
    ): WorkoutSessionItem(
        WorkoutSessionItemType.Exercise,
        "${exerciseTemplate.exercise.name} (${tags?.get(TagCategory.Intensity)?.first() ?: ""})") {

        private var _actualResistance = 0.0 // in KG
        val actualResistance get() = _actualResistance

        private var _actualReps: Int = 0
        val actualReps get() = _actualReps

        private var _notes = ""
        val notes get() = _notes

        private var _isCompleted: Boolean = false
        val isCompleted get() = _isCompleted


        fun getId(): ItemIdentifier {
            return id
        }


        fun setPreviousSession(session: ExerciseSession) {
            previousSession = session
        }


        fun getShortName(): String {
            return exerciseTemplate.exercise.name
        }


        fun getPreviousResistance(): Double? {
            return previousSession?._actualResistance
        }


        fun getPreviousReps(): Int? {
            return previousSession?._actualReps
        }


        fun getPreviousNotes(): String? {
            return if (previousSession == null || previousSession!!._notes.isEmpty()) {
                null
            } else {
                return previousSession?._notes
            }
        }


        fun restore(exerciseSessionId: ItemIdentifier, reps: Int, resistance: Double, notes: String) {
            id = exerciseSessionId
            _actualReps = reps
            _actualResistance = resistance
            _notes = notes

            _isCompleted = true
        }


        // returns true if the ExerciseSession changed
        fun complete(workoutSessionId: ItemIdentifier, reps: Int, resistance: Double, notes: String): ExerciseSessionRecord? {
            if (id == ItemIdentifierGenerator.NO_ID) {
                id = ItemIdentifierGenerator.generateId()
            }

            var updated = false

            if (_actualReps != reps) {
                _actualReps = reps
                updated = true
            }

            if (_actualResistance != resistance) {
                _actualResistance = resistance
                updated = true
            }

            if (_notes != notes) {
                _notes = notes
                updated = true
            }

            _isCompleted = true

            return if (!updated) {
                null
            } else {
                assert(isValid().first)

                ExerciseSessionRecord(
                    id,
                    name,
                    exerciseTemplate.id,
                    workoutSessionId,
                    _actualResistance,
                    _actualReps,
                    _notes,
                    tags
                )
            }
        }


        private fun isValid(): Pair<Boolean, String> {
            if (_actualReps <= 0) {
                return Pair(false, "Reps need to be specified")
            }


            return Pair(true, "")
        }


        companion object {
            fun fromRecord(record: ExerciseSessionRecord, template: ExerciseTemplate): ExerciseSession {
                val session = ExerciseSession(record.id, record.name, template, record.tags, null)

                session.restore(
                    record.id,
                    record.reps,
                    record.resistance,
                    record.notes
                )

                return session
            }
        }
    }


    class RestPeriodSession(
        private val restPeriod: RestPeriod
    ): WorkoutSessionItem(WorkoutSessionItemType.Rest, "Rest") {

        fun getTargetRestPeriod(): IntRange {
            return restPeriod.targetRestPeriodSec
        }


        fun getTargetRestPeriodAsStr(): String {
            return restPeriod.getTargetRestPeriodAsString()
        }
    }
}


@Entity(
    tableName = "exercise_session",
    indices = [
        Index(value = ["exercise_template_id"]),
        Index(value = ["workout_session_id"])
    ],
    foreignKeys = [
        ForeignKey(entity = ExerciseTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("exercise_template_id"), onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = WorkoutSessionRecord::class, parentColumns = arrayOf("id"), childColumns = arrayOf("workout_session_id"), onDelete = ForeignKey.CASCADE)
    ]
)
data class ExerciseSessionRecord(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String,
    @ColumnInfo(name = "exercise_template_id") val exerciseTemplateId: ItemIdentifier,
    @ColumnInfo(name = "workout_session_id") val workoutSessionId: ItemIdentifier,
    val resistance: Double, // in KG
    val reps: Int,
    val notes: String,
    val tags: Tags?
): Item


data class ExerciseResult(
    @ColumnInfo(name = "workout_session_id") val workoutSessionId: ItemIdentifier,
    val reps: Int,
    val resistance: Double // in KG
)


data class ExercisePersonalBest(
    val exerciseName: String,
    val exerciseId: ItemIdentifier,
    val dateMs: Long,
    private val result: ExerciseResult
) {
    val workoutSessionId = result.workoutSessionId
    val reps = result.reps
    val resistance = result.resistance
}


data class ExerciseProgressionPoint(
    val results: List<ExerciseResult>,
    val dateMs: Long
)


data class ExerciseProgression(
    val exerciseName: String,
    val exerciseId: ItemIdentifier,
    val points: List<ExerciseProgressionPoint>
)