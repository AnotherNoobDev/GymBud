package com.example.gymbud.data

import com.example.gymbud.model.Program
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgramRepository {
    private val _programs: MutableStateFlow<List<Program>> = MutableStateFlow(
        ProgramDefaultDatasource.programs.toList()
    )

    val programs: StateFlow<List<Program>> = _programs.asStateFlow()
}