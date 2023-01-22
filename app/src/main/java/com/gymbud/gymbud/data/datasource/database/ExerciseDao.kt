package com.gymbud.gymbud.data.datasource.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: Exercise)

    @Query("UPDATE exercise SET" +
            " name = :name," +
            " notes = :description," +
            " target_muscle = :targetMuscle," +
            " video_tutorial = :videoTutorial" +
            " WHERE id = :id")
    suspend fun update(
        id: ItemIdentifier,
        name: String,
        description: String,
        targetMuscle: MuscleGroup,
        videoTutorial: String
    )

    @Query("DELETE from exercise WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    // note:  Because of the Flow return type, Room also runs the query on the background thread.
    // You don't need to explicitly make it a suspend function and call inside a coroutine scope.
    @Query("SELECT * from exercise WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<Exercise?>

    @Query("SELECT * from exercise ORDER BY name ASC")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * from exercise WHERE name = :name AND notes = :notes AND target_muscle = :targetMuscle AND video_tutorial = :videoTutorial")
    suspend fun hasExerciseWithSameContent(
        name: String,
        notes: String,
        targetMuscle: MuscleGroup,
        videoTutorial: String): Exercise?

    @Query("SELECT COUNT(id) from exercise")
    fun count(): Flow<Int>

    @Query("SELECT * from exercise WHERE id = :id")
    fun getRows(id: ItemIdentifier): Cursor

    @Query("SELECT id from exercise ORDER BY id DESC LIMIT 1")
    suspend fun getMaxId(): ItemIdentifier
}