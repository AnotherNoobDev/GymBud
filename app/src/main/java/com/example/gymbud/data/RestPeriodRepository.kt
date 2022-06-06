package com.example.gymbud.data

import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.RestPeriod
import kotlinx.coroutines.flow.Flow


private const val TAG = "RestPeriodRepo"


class RestPeriodRepository(
    private val restPeriodDao: RestPeriodDao
) {
    val restPeriods: Flow<List<RestPeriod>> = restPeriodDao.getAll()


    suspend fun populateWithDefaults() {
        RestPeriodDefaultDatasource.restPeriods.forEach {
            restPeriodDao.insert(it)
        }
    }

    fun retrieveRestPeriod(id: ItemIdentifier): Flow<RestPeriod?> = restPeriodDao.get(id)


    suspend fun retrieveRestPeriodsOnce(ids: List<ItemIdentifier>): List<RestPeriod> {
        return restPeriodDao.getOnce(ids)
    }
}