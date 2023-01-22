package com.gymbud.gymbud.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gymbud.gymbud.data.datasource.database.ProgramTemplateDao
import com.gymbud.gymbud.data.datasource.database.ProgramTemplateWithItemDao
import com.gymbud.gymbud.data.datasource.defaults.ProgramTemplateDefaultDatasource
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


private const val TAG = "ProgramTemplateRepo"

// todo lots of duplication with SetTemplateRepository atm (basically copy-pasta) -> can we do better? (check after adding real data source)
class ProgramTemplateRepository(
    private var programTemplateDao: ProgramTemplateDao,
    private var programTemplateWithItemDao: ProgramTemplateWithItemDao,
    private val workoutTemplateRepository: WorkoutTemplateRepository,
    private val restPeriodRepository: RestPeriodRepository
) {
    fun setDao(programTemplateDao: ProgramTemplateDao, programTemplateWithItemDao: ProgramTemplateWithItemDao) {
        this.programTemplateDao = programTemplateDao
        this.programTemplateWithItemDao = programTemplateWithItemDao
    }


    val programTemplates: Flow<List<ProgramTemplate>> = programTemplateDao.getAll().map { programs ->
        programs.map {
            populateProgramTemplateItems(it)
            it
        }
    }


    suspend fun populateWithDefaults() {
        ProgramTemplateDefaultDatasource.programs.forEach {
            addProgramTemplate(it.id, it.name, it.items)
        }
    }


    fun retrieveProgramTemplate(id: ItemIdentifier): Flow<ProgramTemplate?> {
        return programTemplateDao.get(id).map { programTemplate ->
            if (programTemplate != null) {
                populateProgramTemplateItems(programTemplate)
            }

            return@map programTemplate
        }
    }


    suspend fun retrieveProgramTemplatesByItem(id: ItemIdentifier): List<Item> {
        return programTemplateDao.getByItem(id)
    }


    private suspend fun populateProgramTemplateItems(programTemplate: ProgramTemplate) {
        val programItems = programTemplateWithItemDao.getAll(programTemplate.id)

        // get set items from db in bulk
        val programWorkoutTemplates = workoutTemplateRepository.retrieveWorkoutTemplates(
            programItems.filter { it.isWithWorkoutTemplate() }.map { it.workoutTemplateId!! }
        )

        val programRestPeriods = restPeriodRepository.retrieveRestPeriods(
            programItems.filter { it.isWithRestPeriod() }.map{ it.restPeriodId!! }
        )

        // put in order
        programItems.forEachIndexed { programItemIndex, programItem ->
            programItem.programItemPosition = programItemIndex // ensure no gaps

            when {
                programItem.isWithWorkoutTemplate() -> {
                    programWorkoutTemplates.find { workoutTemplate -> workoutTemplate.id == programItem.workoutTemplateId  }
                        ?.let { programTemplate.add(it) }
                }
                programItem.isWithRestPeriod() -> {
                    programRestPeriods.find { restPeriod -> restPeriod.id == programItem.restPeriodId  }
                        ?.let { programTemplate.add(it) }
                }
                else -> {
                    assert(false)
                }
            }
        }
    }


    suspend fun hasProgramTemplateWithSameContent(pendingEntry: ProgramTemplate): ProgramTemplate? {
        val emptyTemplate = programTemplateDao.hasProgramTemplateWithSameContent(pendingEntry.name)
            ?: return null

        val template = retrieveProgramTemplate(emptyTemplate.id).first()

        return if (pendingEntry.items == template!!.items) {
            template
        } else {
            null
        }
    }


    suspend fun addProgramTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ): ProgramTemplate {
        return withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, programTemplateDao.getAll().first())
            val entry = ProgramTemplate(id, validName)
            entry.replaceAllWith(items)

            try {
                programTemplateDao.insert(entry)
            } catch (e: SQLiteConstraintException) {
                //Log.e(TAG, "Program template with id: $id already exists!")
                throw e
            }

            items.forEachIndexed { itemIndex, item ->
                try {
                    when (getItemType(item)) {
                        ItemType.WORKOUT_TEMPLATE -> programTemplateWithItemDao.insert(ProgramTemplateWithItem(id, itemIndex, workoutTemplateId = item.id))
                        ItemType.REST_PERIOD -> programTemplateWithItemDao.insert(ProgramTemplateWithItem(id, itemIndex, restPeriodId = item.id))
                        else -> assert(false)
                    }
                } catch (e: SQLiteConstraintException) {
                    //Log.e(TAG, "Failed to link item with id: ${item.id} to program $id")
                    throw e
                }
            }

            return@withContext entry
        }
    }


    suspend fun updateProgramTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, programTemplateDao.getAll().first())
            programTemplateDao.update(id, validName)

            // first remove older links
            programTemplateWithItemDao.deleteAll(id)

            // then add new links
            val programItemsToAdd = items.mapIndexedNotNull { index, item ->
                when (getItemType(item)) {
                    ItemType.WORKOUT_TEMPLATE -> ProgramTemplateWithItem(id, index, workoutTemplateId = item.id)
                    ItemType.REST_PERIOD -> ProgramTemplateWithItem(id, index, restPeriodId = item.id)
                    else -> null
                }
            }

            programTemplateWithItemDao.insert(programItemsToAdd)
        }
    }


    suspend fun removeProgramTemplate(id: ItemIdentifier): Boolean {
        return programTemplateDao.delete(id) > 0
    }


    suspend fun getMaxId(): ItemIdentifier = programTemplateDao.getMaxId()
}