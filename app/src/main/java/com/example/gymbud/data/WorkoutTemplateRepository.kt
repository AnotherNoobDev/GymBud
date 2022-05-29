package com.example.gymbud.data

import com.example.gymbud.model.Item
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// todo lots of duplication with SetTemplateRepository atm (basically copy-pasta) -> can we do better? (check after adding real data source)
class WorkoutTemplateRepository(
    private val setTemplateRepository: SetTemplateRepository
) {
    private val _workoutTemplates: MutableStateFlow<List<WorkoutTemplate>> = MutableStateFlow(WorkoutTemplateDefaultDatasource.workoutTemplatesForHyperTrophy)
    val workoutTemplates: Flow<List<WorkoutTemplate>> =
        _workoutTemplates.asStateFlow()

    fun retrieveWorkoutTemplate(id: ItemIdentifier): WorkoutTemplate? = _workoutTemplates.value.find { it.id == id }


    fun updateWorkoutTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val workout = retrieveWorkoutTemplate(id)
        workout?.name = name
        workout?.replaceAllWith(items)
    }


    fun addWorkoutTemplate(
        id: ItemIdentifier,
        name: String,
        items: List<Item>
    ) {
        val newWorkout = WorkoutTemplate(id, name)
        newWorkout.replaceAllWith(items)

        val newWorkoutTemplates = _workoutTemplates.value.toMutableSet()
        newWorkoutTemplates.add(newWorkout)
        // todo why complain here about toList but not in other places
        _workoutTemplates.value = newWorkoutTemplates.toList()
    }


    fun removeWorkoutTemplate(id: ItemIdentifier): Boolean {
        val workout = retrieveWorkoutTemplate(id)

        val newWorkoutTemplates = _workoutTemplates.value.toMutableList()
        val removed = newWorkoutTemplates.remove(workout)

        if (removed) {
            _workoutTemplates.value = newWorkoutTemplates
        }

        return removed
    }
}