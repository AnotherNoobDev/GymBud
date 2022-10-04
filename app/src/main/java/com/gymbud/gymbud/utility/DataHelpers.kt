package com.gymbud.gymbud.utility

import android.util.Log
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.datasource.defaults.DEFAULT_HYPERTROPHY_PROGRAM
import com.gymbud.gymbud.data.repository.ProgramTemplateRepository
import com.gymbud.gymbud.data.repository.SessionsRepository
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.flow.first
import java.util.*


// only used for testing --> todo remove this? (some of this stuff might be useful, like suggesting resistance increments, etc?)
suspend fun populateWithSessions(programTemplateRepository: ProgramTemplateRepository, sessionsRepository: SessionsRepository) {
    // get program
    val program = programTemplateRepository.programTemplates.first().filter { it.name == DEFAULT_HYPERTROPHY_PROGRAM }[0]

    // setup start date in the past
    val calendar = Calendar.getInstance()
    calendar[Calendar.YEAR] = 2021
    calendar[Calendar.MONTH] = Calendar.DECEMBER

    // add sessions for ~5 months, with one week rest between every 4 blocks
    val builder = SessionRecordBuilder(calendar)
    val sessions = mutableListOf<WorkoutSessionRecord>()
    val exercisesForSessions = mutableListOf<ExerciseSessionRecord>()

    for(block in (1..20)) {
        if (block % 4 == 0) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        } else {
            val (newSessions, newExercisesForSessions) = generateSessionRecordsForProgramBlock(builder, program)
            sessions += newSessions
            exercisesForSessions += newExercisesForSessions
        }
    }


    // insert in db
    sessions.forEach {
        sessionsRepository.addWorkoutSessionRecord(it)
    }


    exercisesForSessions.forEach {
        sessionsRepository.addExerciseSessionRecord(it)
    }

    Log.i("DataHelpers", "Done")
}


private fun generateSessionRecordsForProgramBlock(builder: SessionRecordBuilder, programTemplate: ProgramTemplate): Pair<List<WorkoutSessionRecord>, List<ExerciseSessionRecord>> {
    val sessions = mutableListOf<WorkoutSessionRecord>()
    val exercisesForSessions = mutableListOf<ExerciseSessionRecord>()


    programTemplate.items.forEach {
        if (it is WorkoutTemplate) {
            val (session, exercises) = generateSessionRecord(builder, programTemplate, it)
            sessions.add(session)
            exercisesForSessions += exercises
        }

        builder.calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    builder.endBlock()

    return Pair(sessions.toList(), exercisesForSessions.toList())
}


private typealias ExerciseResult = Pair<Int, Double>

private class SessionRecordBuilder(
    val calendar: Calendar
) {
    private val exercises = mutableListOf<ExerciseTemplate>()
    private val warmupRepsAndResistanceForExercise = mutableMapOf<ItemIdentifier, ExerciseResult>()
    private val workingRepsAndResistanceForExercise = mutableMapOf<ItemIdentifier, ExerciseResult>()

    private var sessionId = ItemIdentifierGenerator.NO_ID
    private val records = mutableListOf<ExerciseSessionRecord>()


    fun getSessionId(): ItemIdentifier {
        return sessionId
    }


    fun beginSession(session: ItemIdentifier) {
        sessionId = session
        records.clear()
    }


    fun add(exerciseRecord: ExerciseSessionRecord) {
        records.add(exerciseRecord)
    }


    fun endSession(): List<ExerciseSessionRecord> {
        return records.toList()
    }


    fun endBlock() {
        incrementValues()
    }


    private fun incrementValues() {
        exercises.forEach {
            val (nextWarmup, nextWorking) = incrementValuesForExercise(it, workingRepsAndResistanceForExercise[it.id]!!)
            warmupRepsAndResistanceForExercise[it.id] = nextWarmup
            workingRepsAndResistanceForExercise[it.id] = nextWorking
        }
    }


    private fun incrementValuesForExercise(exerciseTemplate: ExerciseTemplate, working: ExerciseResult): Pair<ExerciseResult, ExerciseResult> {
        val nextWorking = if (working.first < exerciseTemplate.targetRepRange.last) {
            Pair(working.first + 2, working.second)
        } else {
            Pair(exerciseTemplate.targetRepRange.first, incrementResistance(working.second))
        }

        val nextWarmup = Pair(10, getWarmupForResistance(nextWorking.second))

        return Pair(nextWarmup, nextWorking)
    }


    private fun incrementResistance(v: Double): Double = v + 2.5

    private fun getWarmupForResistance(workingValue: Double): Double = workingValue / 2.0


    fun getRepsAndResistanceForExercise(exerciseTemplate: ExerciseTemplate, tags: Tags?): ExerciseResult {
        if (!exercises.contains(exerciseTemplate)) {
            exercises.add(exerciseTemplate)
            warmupRepsAndResistanceForExercise[exerciseTemplate.id] = generateWarmupRepsAndResistanceForExercise(exerciseTemplate)
            workingRepsAndResistanceForExercise[exerciseTemplate.id] = generateWorkingRepsAndResistanceForExercise(exerciseTemplate)
        }

        return if ((tags != null) && (tags[TagCategory.Intensity]?.contains("Warmup") == true)) {
            warmupRepsAndResistanceForExercise[exerciseTemplate.id]!!
        } else {
            workingRepsAndResistanceForExercise[exerciseTemplate.id]!!
        }
    }


    private fun generateWarmupRepsAndResistanceForExercise(exerciseTemplate: ExerciseTemplate): ExerciseResult = Pair(exerciseTemplate.targetRepRange.random() / 2, 10.0)


    private fun generateWorkingRepsAndResistanceForExercise(exerciseTemplate: ExerciseTemplate): ExerciseResult = Pair(exerciseTemplate.targetRepRange.random(), 20.0)
}


private fun generateSessionRecord(builder: SessionRecordBuilder, programTemplate: ProgramTemplate, workoutTemplate: WorkoutTemplate): Pair<WorkoutSessionRecord, List<ExerciseSessionRecord>> {
    // workout sessions record
    val sessionId = ItemIdentifierGenerator.generateId()
    val session  = WorkoutSessionRecord(
        sessionId,
        workoutTemplate.name,
        workoutTemplate.id,
        programTemplate.id,
        builder.calendar.time.time,
        generateRandomSessionDurationMS(60,120),
        generateRandomNotes()
    )

    // exercises records
    builder.beginSession(sessionId)
    workoutTemplate.items.forEach {
        when(it) {
            is TaggedItem -> addItemToSession(builder, it.item, it.tags)
            else -> addItemToSession(builder, it, null)
        }

    }

    return Pair(session, builder.endSession())
}


@Suppress("SameParameterValue")
private fun generateRandomSessionDurationMS(minMinutes: Int, maxMinutes: Int): Long {
    return ((minMinutes..maxMinutes).random() * 60 * 1000).toLong()
}


private fun generateRandomNotes(): String {
    return "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
            "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
}


private fun addItemToSession(sessionRecordBuilder: SessionRecordBuilder, item: Item, tags: Tags?) {
    when (item) {
        is SetTemplate -> addSetTemplateToSession(sessionRecordBuilder, item, tags)
        is ExerciseTemplate -> addExerciseTemplateToSession(sessionRecordBuilder, item, tags)
        is TaggedItem -> addItemToSession(sessionRecordBuilder, item, tags?.plus(item.tags) ?: item.tags)
    }
}


private fun addSetTemplateToSession(sessionRecordBuilder: SessionRecordBuilder, setTemplate: SetTemplate, tags: Tags?) {
    setTemplate.items.forEach {
        addItemToSession(sessionRecordBuilder, it, tags)
    }
}


private fun addExerciseTemplateToSession(sessionRecordBuilder: SessionRecordBuilder, exerciseTemplate: ExerciseTemplate, tags: Tags?) {
    val (reps, resistance) = sessionRecordBuilder.getRepsAndResistanceForExercise(exerciseTemplate, tags)

    sessionRecordBuilder.add(
        ExerciseSessionRecord(
            ItemIdentifierGenerator.generateId(),
            exerciseTemplate.exercise.name,
            exerciseTemplate.id,
            sessionRecordBuilder.getSessionId(),
            resistance,
            reps,
            generateRandomNotes(),
            tags
        )
    )
}

