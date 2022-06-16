package com.example.gymbud.data.repository

import com.example.gymbud.data.datasource.database.RestPeriodDao
import com.example.gymbud.data.datasource.defaults.RestPeriodDefaultDatasource
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.RestPeriod
import kotlinx.coroutines.flow.Flow


//private const val TAG = "RestPeriodRepo"


class RestPeriodRepository(
    private val restPeriodDao: RestPeriodDao
) {
    val restPeriods: Flow<List<RestPeriod>> = restPeriodDao.getAll()

    // todo We always want to have default rest periods in the DB. Where should this be called to ensure that?
    //  can we deploy a pre-populated db? what about interaction with purge() (it would also drop this stuff)
    suspend fun populateWithDefaults() {
        RestPeriodDefaultDatasource.restPeriods.forEach {
            restPeriodDao.insert(it)
        }

        restPeriodDao.insert(RestPeriod.RestDay)
    }


    fun retrieveRestPeriod(id: ItemIdentifier): Flow<RestPeriod?> = restPeriodDao.get(id)


    suspend fun retrieveRestPeriodsOnce(ids: List<ItemIdentifier>): List<RestPeriod> {
        return restPeriodDao.getOnce(ids)
    }
}