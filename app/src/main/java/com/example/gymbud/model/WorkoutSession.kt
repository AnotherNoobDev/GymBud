package com.example.gymbud.model

import android.util.Log
import androidx.room.*
import com.example.gymbud.data.ItemIdentifierGenerator
import java.lang.Exception

import java.util.*


private const val TAG = "WorkoutSession"

enum class WorkoutSessionState {
    NotReady,
    Ready,
    Started,
    Finished
}


class WorkoutSession(
    val workoutTemplate: WorkoutTemplate,
    val programTemplateId: ItemIdentifier,
    previousSession: WorkoutSession?
) {
    private lateinit var startTime: Date
    private var durationMs: Long = 0

    var notes = ""

    private var state: WorkoutSessionState = WorkoutSessionState.Ready

    private var items: List<WorkoutSessionItem>
    private var atItem: Int = -1
    private var atItemProgressionIndex: Int = -1


    init {
        val sessionItemsBuilder: MutableList<WorkoutSessionItem> = mutableListOf()

        // setup from template
        workoutTemplate.items.forEach {
            when(it) {
                is TaggedItem -> addItemToSession(sessionItemsBuilder, it.item, it.tags)
                else -> addItemToSession(sessionItemsBuilder, it, null)
            }
        }

        // add previous session history
        addPreviousSessionHistory(sessionItemsBuilder, previousSession)

        items = sessionItemsBuilder.toList()
    }


    private fun addItemToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, item: Item, tags: Tags?) {
        when (item) {
            is RestPeriod -> addRestPeriodToSession(sessionItemsBuilder, item)
            is SetTemplate -> addSetTemplateToSession(sessionItemsBuilder, item, tags)
            is ExerciseTemplate -> addExerciseTemplateToSession(sessionItemsBuilder, item, tags)
            is TaggedItem -> addItemToSession(sessionItemsBuilder, item, tags?.plus(item.tags) ?: item.tags) // todo not sure if adding tags like this is what I want :)
        }
    }


    private fun addRestPeriodToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, restPeriod: RestPeriod) {
        sessionItemsBuilder.add(WorkoutSessionItem.RestPeriodSession(restPeriod))
    }


    private fun addSetTemplateToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, setTemplate: SetTemplate, tags: Tags?) {
        setTemplate.items.forEach {
            addItemToSession(sessionItemsBuilder, it, tags)
        }
    }


    private fun addExerciseTemplateToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, exerciseTemplate: ExerciseTemplate, tags: Tags?) {
        sessionItemsBuilder.add(
            WorkoutSessionItem.ExerciseSession(
                exerciseTemplate.exercise.name,
                exerciseTemplate,
                tags,
                null
            )
        )
    }


    private fun addPreviousSessionHistory(sessionItemsBuilder: MutableList<WorkoutSessionItem>, previousSession: WorkoutSession?) {
        if (previousSession == null) {
            return
        }

        // a previous session must always be built using the current Workout Template
        // so the new session and previous session should always have the same items
        if (sessionItemsBuilder.size != previousSession.items.size) {
            val msg = "Previous session length (no. items) doesn't match current session length!"
            Log.e(TAG, msg)
            throw Exception(msg)
        }


        sessionItemsBuilder.zip(previousSession.items).forEach { (current, prev) ->
            if (current is WorkoutSessionItem.ExerciseSession) {
                if (prev !is WorkoutSessionItem.ExerciseSession) {
                    val msg = "WorkoutSessionItem type mismatch between previous session and current session"
                    Log.e(TAG, msg)
                    throw Exception(msg)
                }

                if (current.getShortName() != prev.getShortName()) {
                    val msg = "WorkoutSessionItem name mismatch between previous session and current session"
                    Log.e(TAG, msg)
                    throw Exception(msg)
                }

                current.setPreviousSession(prev)
            }
        }
    }


    fun getShortName(): String {
        return workoutTemplate.name
    }


    fun getStartTime(): Date {
        return startTime
    }


    fun start() {
        assert(state == WorkoutSessionState.Ready)

        startTime = Date()
        atItem = 0
        atItemProgressionIndex = atItem

        state = WorkoutSessionState.Started
    }


    /**
     * Use this to continue a partial session from where it left off
     */
    fun restart(other: WorkoutSession, fromItem: Int) {
        assert (state == WorkoutSessionState.Ready)
        assert (items.size == other.items.size)

        startTime = Date(Date().time - other.durationMs)

        // fill items with data from partial session
        var lastCompleted = 0
        other.items.forEachIndexed { at, item ->
            if (item is WorkoutSessionItem.ExerciseSession && item.isCompleted) {
                if (at > lastCompleted) {
                    lastCompleted = at
                }
            }
        }

        for (at in 0..lastCompleted) {
            val item = items[at]
            if (item is WorkoutSessionItem.ExerciseSession) {
                assert(other.items[at] is WorkoutSessionItem.ExerciseSession)
                val otherItem = other.items[at] as WorkoutSessionItem.ExerciseSession

                item.complete(otherItem.actualReps, otherItem.actualResistance, otherItem.notes)
            }
        }

        // set current item
        atItem = if (fromItem >= 0) {
            fromItem
        } else {
            // go to last completed item
            lastCompleted
        }

        assert(atItem >= 0 && atItem < items.size)

        atItemProgressionIndex = atItem

        state = WorkoutSessionState.Started
    }


    fun getCurrentItem(): WorkoutSessionItem {
        assert(state == WorkoutSessionState.Started)

        return items[atItem]
    }


    fun getCurrentItemIndex(): Int {
        return atItem
    }


    fun getProgressedToItemIndex(): Int {
        return atItemProgressionIndex
    }


    fun hasPreviousItem(): Boolean {
        assert(state != WorkoutSessionState.NotReady)

        return (getPreviousItem() != null)
    }


    fun getPreviousItemType(): WorkoutSessionItemType? {
        assert(state != WorkoutSessionState.NotReady)

        return getPreviousItem()?.type
    }


    private fun getPreviousItem(): WorkoutSessionItem? {
        return if (atItem > 0) {
            items[atItem - 1]
        } else {
            null
        }
    }


    fun hasNextItem(): Boolean {
        assert(state != WorkoutSessionState.NotReady)

        return atItem < items.size - 1
    }


    fun getNextItemHint(): String {
        assert(state != WorkoutSessionState.NotReady)

        return if (atItem < items.size - 1) {
            items[atItem + 1].hint
        } else {
            ""
        }
    }


    fun getNextItemType(): WorkoutSessionItemType? {
        assert(state != WorkoutSessionState.NotReady)

        return if (atItem < items.size - 1) {
            items[atItem + 1].type
        } else {
            null
        }
    }


    fun goBack() {
        assert(state == WorkoutSessionState.Started)

        if (atItem > 0) {
            atItem--
        }
    }


    fun proceed() {
        assert(state == WorkoutSessionState.Started)

        if (atItem < items.size) {
            atItem++
        }

        if (atItem > atItemProgressionIndex) {
            atItemProgressionIndex = atItem
        }
    }


    fun resume() {
        atItem = atItemProgressionIndex
    }


    fun goToItem(itemIndex: Int) {
        if (itemIndex < 0) {
            atItem = 0
            return
        }

        if (itemIndex > atItemProgressionIndex) {
            atItem = atItemProgressionIndex
            return
        }

        atItem = itemIndex
    }


    fun finish() {
        assert(state == WorkoutSessionState.Started)

        durationMs = Date().time - startTime.time

        state = WorkoutSessionState.Finished
    }


    fun getDuration(): Long {
        assert(state == WorkoutSessionState.Finished)

        return durationMs
    }


    fun getResults(): List<WorkoutSessionItem.ExerciseSession> {
        assert(state == WorkoutSessionState.Finished)

        return items.mapNotNull {
            if (it is WorkoutSessionItem.ExerciseSession && it.isCompleted) {
                it
            } else {
                null
            }
        }
    }


    fun finalize(): Pair<WorkoutSessionRecord, List<ExerciseSessionRecord>> {
        assert(state == WorkoutSessionState.Finished)

        val workoutSessionId = ItemIdentifierGenerator.generateId()

        return Pair(
            WorkoutSessionRecord(
                workoutSessionId,
                workoutTemplate.name,
                workoutTemplate.id,
                programTemplateId,
                startTime.time,
                durationMs,
                notes
            ),
            items.mapNotNull {
                if (it is WorkoutSessionItem.ExerciseSession && it.isCompleted) {
                    it.finalize(workoutSessionId)
                } else {
                    null
                }
            }
        )
    }


    fun getExerciseSessions(): List<WorkoutSessionItem.ExerciseSession> {
        return items.mapNotNull {
            when(it) {
                is WorkoutSessionItem.ExerciseSession -> it
                else -> null
            }
        }
    }


    fun getItems(): List<WorkoutSessionItem> {
        return items
    }


    companion object {
        fun fromRecord(record: WorkoutSessionRecord, template: WorkoutTemplate, exerciseRecords: List<ExerciseSessionRecord>): WorkoutSession {
            // workout session
            val session = WorkoutSession(template, record.programTemplateId, null)
            session.state = WorkoutSessionState.Finished
            session.startTime = Date(record.date)
            session.durationMs = record.durationMs
            session.notes = record.notes

            // the session item list is built according to the current template, but does not yet contain previous exercise records
            // we restore them next, but we must be careful since the previous records could have been generated from an older version of the workout template

            val restoredItems = mutableListOf<WorkoutSessionItem>()

            // group records by template --> robustness to changing Set order within Workout
            val exerciseRecordsByTemplate = exerciseRecords.groupByTo(mutableMapOf()) { it.exerciseTemplateId }

            session.items.forEach {
                when (it) {
                    is WorkoutSessionItem.RestPeriodSession -> restoredItems.add(it) // no history for rest periods --> use as-is
                    is WorkoutSessionItem.ExerciseSession -> restoredItems.add(restoreExerciseSessionFromRecord(it, exerciseRecordsByTemplate))
                }
            }

            // finally swap items from  Workout template with restored items
            session.items = restoredItems
            session.atItem = restoredItems.size - 1
            session.atItemProgressionIndex = session.atItem

            return session
        }


        private fun restoreExerciseSessionFromRecord(
            exerciseSession: WorkoutSessionItem.ExerciseSession,
            exerciseRecordsByTemplate: MutableMap<ItemIdentifier, MutableList<ExerciseSessionRecord>>
        ): WorkoutSessionItem.ExerciseSession {

            val exerciseRecordsForTemplate = exerciseRecordsByTemplate[exerciseSession.exerciseTemplate.id]?: return exerciseSession
            if (exerciseRecordsForTemplate.isEmpty()) return exerciseSession

            val exerciseRecordToUse =  when ((exerciseSession.tags?.get(TagCategory.Intensity)?.first() ?: "")) {
                SetIntensity.Warmup.toString() -> {
                    // use atRecord only if it has intensity warmup or "", otherwise use as-is
                    val atExerciseRecord = exerciseRecordsForTemplate.first()
                    val atExerciseRecordIntensity = (atExerciseRecord.tags?.get(TagCategory.Intensity)?.first() ?: "")

                    if (atExerciseRecordIntensity == SetIntensity.Warmup.toString() || atExerciseRecordIntensity == "") {
                        atExerciseRecord
                    } else {
                        null
                    }
                }

                SetIntensity.Working.toString() -> {
                    // drop atRecord and get next until  we find one with intensity working or "", otherwise use as-is
                    var historyFound = false

                    while (exerciseRecordsForTemplate.isNotEmpty()) {
                        val atExerciseRecord = exerciseRecordsForTemplate.first()
                        val atExerciseRecordIntensity = (atExerciseRecord.tags?.get(TagCategory.Intensity)?.first() ?: "")

                        if (atExerciseRecordIntensity == SetIntensity.Warmup.toString()) {
                            exerciseRecordsForTemplate.removeAt(0)
                            continue
                        }

                        historyFound = true
                        break
                    }

                    if (historyFound) {
                        exerciseRecordsForTemplate.first()
                    } else {
                        null
                    }
                }

                else -> {
                    // use atRecord if available, or use as-is
                    exerciseRecordsForTemplate.first()
                }
            }

            return if (exerciseRecordToUse != null) {
                exerciseRecordsForTemplate.removeAt(0)
                WorkoutSessionItem.ExerciseSession.fromRecord(exerciseRecordToUse, exerciseSession.exerciseTemplate)
            } else {
                // we didn't find any history for the exercise --> use as-is
                exerciseSession
            }
        }
    }
}


@Entity(
    tableName = "workout_session",
    indices = [
        Index(value = ["workout_template_id"]),
    ],
    foreignKeys = [
        ForeignKey(entity = WorkoutTemplate::class, parentColumns = arrayOf("id"), childColumns = arrayOf("workout_template_id"), onDelete = ForeignKey.CASCADE),
    ]
)
data class WorkoutSessionRecord(
    @PrimaryKey(autoGenerate = false) override val id: ItemIdentifier,
    override var name: String,
    @ColumnInfo(name = "workout_template_id") val workoutTemplateId: ItemIdentifier,
    @ColumnInfo(name = "program_template_id") val programTemplateId: ItemIdentifier,
    val date: Long,
    val durationMs: Long,
    val notes: String
): Item


data class DayOfTheMonth(
    val day: Int,
    val workoutSessionsId: ItemIdentifier,
    val workoutSessionName: String
)


data class PartialWorkoutSessionRecord (
    val workoutSessionId: ItemIdentifier,
    val atItem: Int,
    val restTimerStartMs: Long
)