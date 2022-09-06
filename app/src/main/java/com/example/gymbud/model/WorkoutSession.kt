package com.example.gymbud.model

import android.annotation.SuppressLint
import android.util.Log
import androidx.room.*
import com.example.gymbud.data.ItemIdentifierGenerator
import java.lang.Exception
import java.text.SimpleDateFormat

import java.util.*


private const val TAG = "WorkoutSession"

enum class WorkoutSessionState {
    NotReady,
    Ready,
    Started,
    Finished
}


// todo Session needs to survive changing apps, accidental close, etc..
class WorkoutSession(
    private val workoutTemplate: WorkoutTemplate,
    private val programTemplateId: ItemIdentifier,
    previousSession: WorkoutSession?
) {
    @SuppressLint("SimpleDateFormat")
    private val todayStr = SimpleDateFormat("dd-MM-yyyy").format(Date())
    private lateinit var startTime: Date
    private var durationMs: Long = 0

    var notes = ""

    private var state: WorkoutSessionState = WorkoutSessionState.Ready

    private var items: List<WorkoutSessionItem>
    private var atItem: Int = -1


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
                exerciseTemplate.name + todayStr,
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

        state = WorkoutSessionState.Started
    }


    fun getCurrentItem(): WorkoutSessionItem {
        assert(state == WorkoutSessionState.Started)

        return items[atItem]
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


    fun proceed() {
        assert(state == WorkoutSessionState.Started)

        if (atItem < items.size) {
            atItem++
        }
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
                workoutTemplate.name + todayStr,
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


    companion object {
        /*
        private fun restoreExerciseSessionFromRecord(exerciseSession: WorkoutSessionItem.ExerciseSession, exerciseRecords: List<ExerciseSessionRecord>): WorkoutSessionItem.ExerciseSession {

        }
         */


        fun fromRecord(record: WorkoutSessionRecord, template: WorkoutTemplate, exerciseRecords: List<ExerciseSessionRecord>): WorkoutSession {
            // workout session
            val session = WorkoutSession(template, record.programTemplateId, null)
            session.state = WorkoutSessionState.Finished
            session.startTime = Date(record.date)
            session.durationMs = record.durationMs
            session.notes = record.notes

            // the session item list is built according to the current template, but does not yet contain previous exercise records
            // we restore them next, but we must be careful since the previous records could have been generated from an older version of the workout template

            // group records by template --> robustness to changing Set order within Workout
            val exerciseRecordsByTemplate = exerciseRecords.groupByTo(mutableMapOf()) { it.exerciseTemplateId }

            val restoredItems = mutableListOf<WorkoutSessionItem>()

            session.items.forEach {
                when (it) {
                    is WorkoutSessionItem.RestPeriodSession -> restoredItems.add(it) // no history for rest periods --> use as-is
                    is WorkoutSessionItem.ExerciseSession -> {
                        var historyFound = false

                        val currentIntensity = (it.tags?.get(TagCategory.Intensity)?.first() ?: "")
                        when (currentIntensity) {
                            SetIntensity.Warmup.toString() -> {
                                // use atRecord only if it has intensity warmup or "", otherwise use as-is
                                val atExerciseRecord = exerciseRecordsByTemplate[it.exerciseTemplate.id]?.first()
                                if (atExerciseRecord != null) {
                                    val atExerciseRecordIntensity = (atExerciseRecord.tags?.get(TagCategory.Intensity)?.first() ?: "")

                                    if (atExerciseRecordIntensity == SetIntensity.Warmup.toString() || atExerciseRecordIntensity == "") {
                                        restoredItems.add(WorkoutSessionItem.ExerciseSession.fromRecord(atExerciseRecord, it.exerciseTemplate))
                                        exerciseRecordsByTemplate[it.exerciseTemplate.id]?.removeAt(0)

                                        historyFound = true
                                    }
                                }
                            }
                            SetIntensity.Working.toString() -> {
                                // drop atRecord and get next until  we find one with intensity working or "", otherwise use as-is
                                while (exerciseRecordsByTemplate[it.exerciseTemplate.id]?.isNotEmpty() == true) {
                                    val atExerciseRecord = exerciseRecordsByTemplate[it.exerciseTemplate.id]!!.first()
                                    val atExerciseRecordIntensity = (atExerciseRecord.tags?.get(TagCategory.Intensity)?.first() ?: "")

                                    if (atExerciseRecordIntensity == SetIntensity.Warmup.toString()) {
                                        exerciseRecordsByTemplate[it.exerciseTemplate.id]?.removeAt(0)
                                        continue
                                    }

                                    restoredItems.add(WorkoutSessionItem.ExerciseSession.fromRecord(atExerciseRecord, it.exerciseTemplate))
                                    exerciseRecordsByTemplate[it.exerciseTemplate.id]?.removeAt(0)

                                    historyFound = true
                                    break
                                }
                            }
                            else -> {
                                // use atRecord if available, or use as-is
                                val atExerciseRecord = exerciseRecordsByTemplate[it.exerciseTemplate.id]?.first()
                                if (atExerciseRecord != null) {
                                    restoredItems.add(WorkoutSessionItem.ExerciseSession.fromRecord(atExerciseRecord, it.exerciseTemplate))
                                    exerciseRecordsByTemplate[it.exerciseTemplate.id]?.removeAt(0)

                                    historyFound = true
                                }
                            }
                        }

                        if (!historyFound) {
                            // we didn't find any history for the exercise --> use as-is
                            restoredItems.add(it)
                        }
                    }
                }
            }

            // finally swap items from  Workout template with restored items
            session.items = restoredItems
            session.atItem = restoredItems.size - 1

            return session
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