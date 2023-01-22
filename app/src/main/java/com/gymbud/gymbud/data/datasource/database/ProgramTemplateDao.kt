package com.gymbud.gymbud.data.datasource.database

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramTemplateDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(programTemplate: ProgramTemplate)

    @Query("UPDATE program_template SET" +
            " name = :name" +
            " WHERE id = :id")
    suspend fun update(
        id: ItemIdentifier,
        name: String
    )

    @Query("DELETE from program_template WHERE id = :id")
    suspend fun delete(id: ItemIdentifier): Int

    @Query("SELECT * from program_template WHERE id = :id")
    fun get(id: ItemIdentifier): Flow<ProgramTemplate?>

    @Query("SELECT program_template.id, program_template.name from program_template " +
            "INNER JOIN program_template_item ON program_template.id=program_template_item.program_template_id  " +
            "WHERE program_template_item.workout_template_id = :id OR program_template_item.rest_period_id = :id")
    suspend fun getByItem(id: ItemIdentifier): List<ItemFromDao>

    @Query("SELECT * from program_template ORDER BY name ASC")
    fun getAll(): Flow<List<ProgramTemplate>>

    @Query("SELECT * from program_template WHERE name = :name")
    suspend fun hasProgramTemplateWithSameContent(name: String): ProgramTemplate?

    @Query("SELECT * from program_template WHERE id = :id")
    fun getRows(id: ItemIdentifier): Cursor

    @Query("SELECT id from program_template ORDER BY id DESC LIMIT 1")
    suspend fun getMaxId(): ItemIdentifier
}


@Dao
interface ProgramTemplateWithItemDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(programTemplateWithItem: ProgramTemplateWithItem)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(programTemplateWithItems: List<ProgramTemplateWithItem>)

    @Query("DELETE from program_template_item WHERE program_template_id = :programTemplateId")
    suspend fun deleteAll(programTemplateId: ItemIdentifier): Int

    @Query("SELECT * from program_template_item WHERE program_template_id = :programTemplateId ORDER BY program_item_pos ASC")
    suspend fun getAll(programTemplateId: ItemIdentifier): List<ProgramTemplateWithItem>

    @Query("SELECT * from program_template_item WHERE program_template_id = :programTemplateId")
    fun getRows(programTemplateId: ItemIdentifier): Cursor
}