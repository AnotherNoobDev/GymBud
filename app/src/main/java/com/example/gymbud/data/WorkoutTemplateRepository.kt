package com.example.gymbud.data

import com.example.gymbud.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutTemplateRepository(
    private val setTemplateRepository: SetTemplateRepository
) {
    private val _workoutTemplates: MutableStateFlow<List<WorkoutTemplate>> = MutableStateFlow(WorkoutTemplateDefaultDatasource.workoutTemplatesForHyperTrophy)
    val workoutTemplates: Flow<List<WorkoutTemplate>> =
        _workoutTemplates.asStateFlow()

    // todo add, remove, update, validate
}