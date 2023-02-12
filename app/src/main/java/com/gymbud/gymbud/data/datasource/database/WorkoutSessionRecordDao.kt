package com.gymbud.gymbud.data.datasource.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.gymbud.gymbud.model.ItemIdentifier
import com.gymbud.gymbud.model.WorkoutSessionRecord


@Dao
interface WorkoutSessionRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutSession: WorkoutSessionRecord)

    @Query( "" +
        "UPDATE workout_session " +
        "SET durationMs = :durationMs, notes = :notes " +
        "WHERE id = :id"
    )
    suspend fun update(
        id: ItemIdentifier,
        durationMs: Long,
        notes: String
    )

    @Query("SELECT * from workout_session WHERE id = :id")
    suspend fun get(id: ItemIdentifier): WorkoutSessionRecord?

    @Query("SELECT date from workout_session WHERE id = :id")
    suspend fun getSessionDate(id: ItemIdentifier): Long?

    @Query("DELETE from workout_session WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    @Query("" +
            "SELECT * from workout_session " +
            "WHERE workout_template_id = :workoutTemplateId " +
            "AND NOT id = :activeSessionId " +
            "ORDER BY date DESC " +
            "LIMIT 1")
    suspend fun getPreviousSession(workoutTemplateId: ItemIdentifier, activeSessionId: ItemIdentifier): WorkoutSessionRecord?


    @Query("" +
            "SELECT * from workout_session " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "ORDER BY date ASC ")
    suspend fun getPreviousSessions(startDate: Long, endDate: Long): List<WorkoutSessionRecord>


    @RawQuery
    suspend fun getPreviousSessionsByFilters(query: SupportSQLiteQuery): List<ItemIdentifier>

    @Query("SELECT id from workout_session ORDER BY id DESC LIMIT 1")
    suspend fun getMaxId(): ItemIdentifier
}