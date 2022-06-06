package com.example.gymbud.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutTemplate: WorkoutTemplate)

    @Query("UPDATE workout_template SET" +
            " name = :name" +
            " WHERE id = :id")
    suspend fun update(
        id: ItemIdentifier,
        name: String
    )

    @Query("DELETE from workout_template WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    @Query("SELECT * from workout_template WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<WorkoutTemplate?>

    @Query("SELECT * from workout_template ORDER BY name ASC")
    fun getAll(): Flow<List<WorkoutTemplate>>

    @Query("SELECT * from workout_template ORDER BY name ASC")
    fun getAllOnce(): List<WorkoutTemplate>

    @Query("SELECT COUNT(id) from workout_template")
    fun count(): Flow<Int>
}


@Dao
interface WorkoutTemplateWithItemDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutTemplateWithItem: WorkoutTemplateWithItem)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(workoutTemplateWithItems: List<WorkoutTemplateWithItem>)

    @Query("DELETE from workout_template_item WHERE workout_template_id = :workoutTemplateId")
    suspend fun deleteAll(workoutTemplateId: ItemIdentifier): Int

    @Query("SELECT * from workout_template_item WHERE workout_template_id = :workoutTemplateId ORDER BY workout_item_pos ASC")
    suspend fun getAllOnce(workoutTemplateId: ItemIdentifier): List<WorkoutTemplateWithItem>
}