package com.example.gymbud.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.data.datasource.database.ExerciseSessionRecordDao
import com.example.gymbud.data.datasource.database.WorkoutSessionRecordDao
import com.example.gymbud.model.*
import com.example.gymbud.ui.viewmodel.ExerciseFilters
import com.example.gymbud.utility.getDayOfMonth
import com.example.gymbud.utility.getMonthSpan
import kotlinx.coroutines.flow.first


//private const val TAG = "SessionsRepo"


class SessionsRepository(
    private val exerciseSessionRecordDao: ExerciseSessionRecordDao,
    private val workoutSessionRecordDao: WorkoutSessionRecordDao,
    private val workoutTemplateRepository: WorkoutTemplateRepository
) {
    suspend fun addExerciseSessionRecord(record: ExerciseSessionRecord) {
        exerciseSessionRecordDao.insert(record)
    }


    suspend fun addWorkoutSessionRecord(record: WorkoutSessionRecord) {
        workoutSessionRecordDao.insert(record)
    }


    suspend fun getWorkoutSession(workoutSessionId: ItemIdentifier): WorkoutSession? {
        val record = workoutSessionRecordDao.get(workoutSessionId)
            ?: return null

        val template = workoutTemplateRepository.retrieveWorkoutTemplate(record.workoutTemplateId).first()
            ?: return null

        val exerciseRecords = exerciseSessionRecordDao.getFromSession(record.id)
        if (exerciseRecords.isEmpty()) {
            return null
        }

        return WorkoutSession.fromRecord(record, template, exerciseRecords)
    }


    suspend fun getWorkoutSessionDate(workoutSessionId: ItemIdentifier): Long? {
        return workoutSessionRecordDao.getSessionDate(workoutSessionId)
    }


    suspend fun getPreviousWorkoutSession(workoutTemplateId: ItemIdentifier): WorkoutSession? {
        val prevSesRecord = workoutSessionRecordDao.getPreviousSession(workoutTemplateId)
            ?: return null

        val template = workoutTemplateRepository.retrieveWorkoutTemplate(prevSesRecord.workoutTemplateId).first()
            ?: return null

        val prevSesExerciseRecords = exerciseSessionRecordDao.getFromSession(prevSesRecord.id)
        if (prevSesExerciseRecords.isEmpty()) {
            return null
        }

        return WorkoutSession.fromRecord(prevSesRecord, template, prevSesExerciseRecords)
    }


    suspend fun getSessionsByMonth(year: Int, month: Int, daySpan: Int): List<DayOfTheMonth> {
        // determine start and end date of month span
        val (startDate, endDate, daysInMonth) = getMonthSpan(year, month, daySpan)

        // fill with empty days
        val sessionDays = mutableListOf<DayOfTheMonth>()
        daysInMonth.forEach {
            sessionDays.add(DayOfTheMonth(it, ItemIdentifierGenerator.NO_ID, ""))
        }

        // query for all sessions within that time period (sessions are in order)
        val sessions = workoutSessionRecordDao.getPreviousSessions(startDate, endDate)

        // fill days with sessions
        var atDay = 0
        sessions.forEach { session ->
            for (day in atDay until daySpan) {
                if (getDayOfMonth(session.date) == sessionDays[day].day) {
                    sessionDays[day] = DayOfTheMonth(sessionDays[day].day, session.id, session.name)
                    atDay = day + 1
                    break
                }
            }
        }

        return sessionDays.toList()
    }

    /**
     * exerciseTemplates: the templates associated with this exercise
     */
    suspend fun getExercisePersonalBest(exerciseTemplates: List<ItemIdentifier>, filters: ExerciseFilters): ExerciseResult? {
        // restrict usable workout sessions by filters
        val sessions = getSessionIdsByFilters(filters)

        return exerciseSessionRecordDao.getExercisePersonalBest(exerciseTemplates, sessions)
    }


    /**
     * exerciseTemplates: the templates associated with this exercise
     */
    suspend fun getExerciseResults(exerciseTemplates: List<ItemIdentifier>, filters: ExerciseFilters): List<ExerciseResult> {
        // restrict usable workout sessions by filters
        val sessions = getSessionIdsByFilters(filters)

        return exerciseSessionRecordDao.getExerciseResults(exerciseTemplates, sessions)
    }


    private suspend fun getSessionIdsByFilters(filters: ExerciseFilters): List<ItemIdentifier> {
        // construct query based on filters
        var queryString = ""
        val queryArgs = mutableListOf<Any>()
        var containsCondition = false

        queryString += "SELECT id from workout_session"

        if (filters.programTemplateId != null) {
            queryString += " WHERE"
            queryString += " program_template_id IS ?"
            queryArgs.add(filters.programTemplateId)
            containsCondition = true
        }

        if (filters.periodStart != null) {
            queryString += if (containsCondition) {
                " AND"
            } else {
                containsCondition = true
                " WHERE"
            }

            queryString += " date >= ?"
            queryArgs.add(filters.periodStart)
        }

        if (filters.periodEnd != null) {
            queryString += if (containsCondition) {
                " AND"
            } else {
                // suppressing this, value is set intentionally to avoid future bugs when extending :)
                @Suppress("UNUSED_VALUE")
                containsCondition = true
                " WHERE"
            }

            queryString += " date <= ?"
            queryArgs.add(filters.periodEnd)
        }

        queryString += ";"

        val query = SimpleSQLiteQuery(queryString, queryArgs.toTypedArray())
        return workoutSessionRecordDao.getPreviousSessionsByFilters(query)
    }


    suspend fun removeSession(id: ItemIdentifier): Boolean {
        return workoutSessionRecordDao.delete(id) > 0
    }
}