package com.example.gymbud.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.RestPeriod
import kotlinx.coroutines.flow.Flow


@Dao
interface RestPeriodDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(restPeriod: RestPeriod)

    @Query("SELECT * from rest_period WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<RestPeriod?>

    /*
    @Query("SELECT * from exercise WHERE id = :id")
    suspend fun getOnce(id:ItemIdentifier): Exercise?
    */

    @Query("SELECT * from rest_period WHERE id IN (:ids)")
    suspend fun getOnce(ids: List<ItemIdentifier>): List<RestPeriod>

    @Query("SELECT * from rest_period ORDER BY name ASC")
    fun getAll(): Flow<List<RestPeriod>>

    /*
    @Query("SELECT * from exercise ORDER BY name ASC")
    suspend fun getAllOnce(): List<Exercise>
    */
}