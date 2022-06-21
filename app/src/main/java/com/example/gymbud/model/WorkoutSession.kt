package com.example.gymbud.model

import android.annotation.SuppressLint
import android.util.Log
import androidx.room.*
import com.example.gymbud.data.ItemIdentifierGenerator
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
                is TaggedItem -> addItemToSession(sessionItemsBuilder, it.item)
                else -> addItemToSession(sessionItemsBuilder, it)
            }
        }

        // add previous session history
        addPreviousSessionHistory(sessionItemsBuilder, previousSession)

        items = sessionItemsBuilder.toList()
    }


    private fun addItemToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, item: Item) {
        when (item) {
            is RestPeriod -> addRestPeriodToSession(sessionItemsBuilder, item)
            is SetTemplate -> addSetTemplateToSession(sessionItemsBuilder, item)
            is ExerciseTemplate -> addExerciseTemplateToSession(sessionItemsBuilder, item)
            is TaggedItem -> addItemToSession(sessionItemsBuilder, item.item)
        }
    }


    private fun addRestPeriodToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, restPeriod: RestPeriod) {
        sessionItemsBuilder.add(WorkoutSessionItem.RestPeriodSession(restPeriod))
    }


    private fun addSetTemplateToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, setTemplate: SetTemplate) {
        setTemplate.items.forEach {
            addItemToSession(sessionItemsBuilder, it)
        }
    }


    private fun addExerciseTemplateToSession(sessionItemsBuilder: MutableList<WorkoutSessionItem>, exerciseTemplate: ExerciseTemplate) {
        sessionItemsBuilder.add(
            WorkoutSessionItem.ExerciseSession(
                exerciseTemplate.name + todayStr,
                exerciseTemplate,
                null
            )
        )
    }


    private fun addPreviousSessionHistory(sessionItemsBuilder: MutableList<WorkoutSessionItem>, previousSession: WorkoutSession?) {
        if (previousSession == null) {
            return
        }

        // todo very simplistic at the moment.. assume a 1-to-1 between session items
        if (sessionItemsBuilder.size != previousSession.items.size) {
            Log.w(TAG, "Can't add previous session history: Session length (no. items) has changed from previous session!")
            return
        }


        sessionItemsBuilder.zip(previousSession.items).forEach { (current, prev) ->
            if (current is WorkoutSessionItem.ExerciseSession &&
                prev is WorkoutSessionItem.ExerciseSession &&
                current.getShortName() == prev.getShortName()
            ) {
                current.setPreviousSession(prev)
            }
        }
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
        fun fromRecord(record: WorkoutSessionRecord, template: WorkoutTemplate, exerciseRecords: List<ExerciseSessionRecord>): WorkoutSession {
            // workout session
            val session = WorkoutSession(template, record.programTemplateId, null)
            session.state = WorkoutSessionState.Finished
            session.startTime = Date(record.date)
            session.durationMs = record.durationMs
            session.notes = record.notes

            // workout session items
            var at = 0
            val restoredItems = mutableListOf<WorkoutSessionItem>()
            session.items.forEach {
                when (it) {
                    is WorkoutSessionItem.RestPeriodSession -> restoredItems.add(it)
                    is WorkoutSessionItem.ExerciseSession -> {
                        restoredItems.add(WorkoutSessionItem.ExerciseSession.fromRecord(exerciseRecords[at++], it.exerciseTemplate))
                    }
                }
            }
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