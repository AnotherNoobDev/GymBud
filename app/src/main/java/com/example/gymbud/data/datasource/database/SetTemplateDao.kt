package com.example.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow


@Dao
interface SetTemplateDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(setTemplate: SetTemplate)

    @Query("UPDATE set_template SET" +
            " name = :name" +
            " WHERE id = :id")
    suspend fun update(
        id: ItemIdentifier,
        name: String
    )

    @Query("DELETE from set_template WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    @Query("SELECT * from set_template WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<SetTemplate?>

    @Query("SELECT * from set_template WHERE id IN (:ids)")
    suspend fun getOnce(ids: List<ItemIdentifier>): List<SetTemplate>

    @Query("SELECT * from set_template ORDER BY name ASC")
    fun getAll(): Flow<List<SetTemplate>>

    @Query("SELECT * from set_template ORDER BY name ASC")
    fun getAllOnce(): List<SetTemplate>

    @Query("SELECT COUNT(id) from set_template")
    fun count(): Flow<Int>
}


@Dao
interface SetTemplateWithItemDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(setTemplateWithItem: SetTemplateWithItem)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(setTemplateWithItems: List<SetTemplateWithItem>)

    @Query("DELETE from set_template_item WHERE set_template_id = :setTemplateId")
    suspend fun deleteAll(setTemplateId: ItemIdentifier): Int

    @Query("SELECT * from set_template_item WHERE set_template_id = :setTemplateId ORDER BY set_item_pos ASC")
    suspend fun getAllOnce(setTemplateId: ItemIdentifier): List<SetTemplateWithItem>
}