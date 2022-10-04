package com.gymbud.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymbud.gymbud.model.ExerciseResult
import com.gymbud.gymbud.model.ExerciseSessionRecord
import com.gymbud.gymbud.model.ItemIdentifier

@Dao
interface ExerciseSessionRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exerciseSession: ExerciseSessionRecord)

    @Query("SELECT * from exercise_session WHERE workout_session_id = :workoutSessionId")
    suspend fun getFromSession(workoutSessionId: ItemIdentifier): List<ExerciseSessionRecord>

    // todo: replace with raw query and use ExerciseEvaluators.calculateOneRepMax instead of naked expression in ORDER BY
    @Query("SELECT workout_session_id, reps, resistance from exercise_session " +
            "WHERE exercise_template_id IN (:exerciseTemplates) " +
            "AND workout_session_id IN (:workoutSessions) " +
            "ORDER BY resistance * (1 + reps / 30.0) DESC " +
            "LIMIT 1")
    suspend fun getExercisePersonalBest(exerciseTemplates: List<ItemIdentifier>, workoutSessions: List<ItemIdentifier> ): ExerciseResult?


    @Query("SELECT workout_session_id, reps, resistance from exercise_session " +
            "WHERE exercise_template_id IN (:exerciseTemplates) " +
            "AND workout_session_id IN (:workoutSessions)")
    suspend fun getExerciseResults(exerciseTemplates: List<ItemIdentifier>, workoutSessions: List<ItemIdentifier> ): List<ExerciseResult>
}
