package com.example.gymbud.data

import com.example.gymbud.model.Exercise
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.RestPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "RestPeriodRepository"

class RestPeriodRepository {
    private val _restPeriods: MutableStateFlow<List<RestPeriod>> = MutableStateFlow(RestPeriodDefaultDatasource.restPeriods)
    val restPeriods: StateFlow<List<RestPeriod>> = _restPeriods.asStateFlow()

    fun retrieveRestPeriod(id: ItemIdentifier): RestPeriod? = _restPeriods.value.find { it.id == id }

    // todo
}