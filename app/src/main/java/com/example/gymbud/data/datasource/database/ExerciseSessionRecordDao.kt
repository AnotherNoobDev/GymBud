package com.example.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.ExerciseSessionRecord
import com.example.gymbud.model.ItemIdentifier

@Dao
interface ExerciseSessionRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exerciseSession: ExerciseSessionRecord)

    @Query("SELECT * from exercise_session WHERE workout_session_id = :workoutSessionId")
    suspend fun getFromSession(workoutSessionId: ItemIdentifier): List<ExerciseSessionRecord>?
}
