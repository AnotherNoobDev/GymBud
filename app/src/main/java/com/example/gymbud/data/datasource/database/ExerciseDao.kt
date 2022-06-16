package com.example.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(exercise: Exercise)

    @Query("UPDATE exercise SET" +
            " name = :name," +
            " notes = :description," +
            " target_muscle = :targetMuscle," +
            " resistance = :resistance" +
            " WHERE id = :id")
    suspend fun update(
        id: ItemIdentifier,
        name: String,
        description: String,
        targetMuscle: MuscleGroup,
        resistance: ResistanceType
    )

    @Query("DELETE from exercise WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    // note:  Because of the Flow return type, Room also runs the query on the background thread.
    // You don't need to explicitly make it a suspend function and call inside a coroutine scope.
    @Query("SELECT * from exercise WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<Exercise?>

    @Query("SELECT * from exercise WHERE id = :id")
    suspend fun getOnce(id:ItemIdentifier): Exercise?

    @Query("SELECT * from exercise ORDER BY name ASC")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * from exercise ORDER BY name ASC")
    suspend fun getAllOnce(): List<Exercise>

    @Query("SELECT COUNT(id) from exercise")
    fun count(): Flow<Int>
}