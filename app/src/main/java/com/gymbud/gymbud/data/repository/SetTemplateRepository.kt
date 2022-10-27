package com.gymbud.gymbud.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.gymbud.gymbud.data.datasource.database.SetTemplateDao
import com.gymbud.gymbud.data.datasource.database.SetTemplateWithItemDao
import com.gymbud.gymbud.data.datasource.defaults.SetTemplateDefaultDatasource
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


private const val TAG = "SetTemplateRepo"

class SetTemplateRepository(
    private val setTemplateDao: SetTemplateDao,
    private val setTemplateWithItemDao: SetTemplateWithItemDao,
    private val exerciseTemplateRepository: ExerciseTemplateRepository,
    private val restPeriodRepository: RestPeriodRepository
) {
    val setTemplates: Flow<List<SetTemplate>> = setTemplateDao.getAll().map { sets ->
        sets.map {
            populateSetTemplateItems(it)
            it
        }
    }


    suspend fun populateWithDefaults() {
        SetTemplateDefaultDatasource.setTemplatesForHypertrophy.forEach {
            addSetTemplate(it.id, it.name, it.items)
        }
    }


    fun retrieveSetTemplate(id: ItemIdentifier): Flow<SetTemplate?> {
        return setTemplateDao.get(id).map { setTemplate ->
            if (setTemplate != null) {
                populateSetTemplateItems(setTemplate)
            }

            return@map setTemplate
        }
    }


    private suspend fun populateSetTemplateItems(setTemplate: SetTemplate) {
        val setItems = setTemplateWithItemDao.getAll(setTemplate.id)

        // get set items from db in bulk
        val setExerciseTemplates = exerciseTemplateRepository.retrieveExerciseTemplates(
            setItems.filter { it.isWithExerciseTemplate() }.map { it.exerciseTemplateId!! }
        )

        val setRestPeriods = restPeriodRepository.retrieveRestPeriods(
            setItems.filter { it.isWithRestPeriod() }.map{ it.restPeriodId!! }
        )

        // put in order
        setItems.forEachIndexed { setItemIndex, setItem ->
            setItem.setItemPosition = setItemIndex // ensure no gaps

            when {
                setItem.isWithExerciseTemplate() -> {
                    setExerciseTemplates.find { exerciseTemplate -> exerciseTemplate.id == setItem.exerciseTemplateId  }
                        ?.let { setTemplate.add(it) }
                }
                setItem.isWithRestPeriod() -> {
                    setRestPeriods.find { restPeriod -> restPeriod.id == setItem.restPeriodId  }
                        ?.let { setTemplate.add(it) }
                }
                else -> {
                    assert(false)
                }
            }
        }
    }


    suspend fun retrieveSetTemplatesByItem(id: ItemIdentifier): List<Item> {
        return setTemplateDao.getByItem(id)
    }


    suspend fun retrieveSetTemplates(ids: List<ItemIdentifier>): List<SetTemplate> {
        val templates = setTemplateDao.get(ids)
        templates.forEach {
            populateSetTemplateItems(it)
        }

        return templates
    }


    suspend fun addSetTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, setTemplateDao.getAll().first())
            try {
                setTemplateDao.insert(SetTemplate(id, validName))
            } catch (e: SQLiteConstraintException) {
                //Log.e(TAG, "Set template with id: $id already exists!")
                throw e
            }

            items.forEachIndexed { itemIndex, item ->
                try {
                    when (getItemType(item)) {
                        ItemType.EXERCISE_TEMPLATE -> setTemplateWithItemDao.insert(SetTemplateWithItem(id, itemIndex, exerciseTemplateId = item.id))
                        ItemType.REST_PERIOD -> setTemplateWithItemDao.insert(SetTemplateWithItem(id, itemIndex, restPeriodId = item.id))
                        else -> assert(false)
                    }
                } catch (e: SQLiteConstraintException) {
                    //Log.e(TAG, "Failed to link item with id: ${item.id} to set $id")
                    throw e
                }
            }
        }
    }


    suspend fun updateSetTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        withContext(Dispatchers.IO) {
            val validName = getValidName(id, name, setTemplateDao.getAll().first())
            setTemplateDao.update(id, validName)

            // first remove older links
            setTemplateWithItemDao.deleteAll(id)

            // then add new links
            val setItemsToAdd = items.mapIndexedNotNull { index, item ->
                when (getItemType(item)) {
                    ItemType.EXERCISE_TEMPLATE -> SetTemplateWithItem(id, index, exerciseTemplateId = item.id)
                    ItemType.REST_PERIOD -> SetTemplateWithItem(id, index, restPeriodId = item.id)
                    else -> null
                }
            }

            setTemplateWithItemDao.insert(setItemsToAdd)
        }
    }


    suspend fun removeSetTemplate(id: ItemIdentifier): Boolean {
        return setTemplateDao.delete(id) > 0
    }
}