package com.example.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WorkoutSessionRecord


@Dao
interface WorkoutSessionRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutSession: WorkoutSessionRecord)

    @Query("SELECT * from workout_session WHERE id = :id")
    suspend fun get(id: ItemIdentifier): WorkoutSessionRecord?

    @Query("DELETE from workout_session WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    @Query("" +
            "SELECT * from workout_session " +
            "WHERE workout_template_id = :workoutTemplateId " +
            "ORDER BY date DESC " +
            "LIMIT 1")
    suspend fun getPreviousSession(workoutTemplateId: ItemIdentifier): WorkoutSessionRecord?


    @Query("" +
            "SELECT * from workout_session " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "ORDER BY date ASC ")
    suspend fun getPreviousSessions(startDate: Long, endDate: Long): List<WorkoutSessionRecord>
}