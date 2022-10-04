package com.gymbud.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymbud.gymbud.model.*
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
    suspend fun get(ids: List<ItemIdentifier>): List<SetTemplate>

    @Query("SELECT set_template.id, set_template.name from set_template " +
            "INNER JOIN set_template_item ON set_template.id=set_template_item.set_template_id  " +
            "WHERE set_template_item.exercise_template_id = :id OR set_template_item.rest_period_id = :id")
    suspend fun getByItem(id: ItemIdentifier): List<ItemFromDao>

    @Query("SELECT * from set_template ORDER BY name ASC")
    fun getAll(): Flow<List<SetTemplate>>
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
    suspend fun getAll(setTemplateId: ItemIdentifier): List<SetTemplateWithItem>
}