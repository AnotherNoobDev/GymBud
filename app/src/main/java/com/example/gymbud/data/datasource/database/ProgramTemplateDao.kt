package com.example.gymbud.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gymbud.model.*
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

    @Query("SELECT * from program_template ORDER BY name ASC")
    fun getAll(): Flow<List<ProgramTemplate>>
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
}