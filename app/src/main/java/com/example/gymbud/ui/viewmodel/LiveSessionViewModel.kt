package com.example.gymbud.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.repository.SessionsRepository
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class LiveSessionViewModel(
    private val sessionRepository: SessionsRepository
): ViewModel() {

    private val _state: MutableStateFlow<WorkoutSessionState> = MutableStateFlow(WorkoutSessionState.NotReady)
    val state: Flow<WorkoutSessionState> = _state

    private val _previousSession: MutableStateFlow<WorkoutSession?> = MutableStateFlow(null)
    val previousSession: Flow<WorkoutSession?> = _previousSession

    private var _workoutSession: WorkoutSession? = null
    private val workoutSession get() = _workoutSession!!


    suspend fun prepare(workoutTemplate: WorkoutTemplate, programTemplateId: ItemIdentifier)  {
        val prev = sessionRepository.getPreviousWorkoutSession(workoutTemplate.id)
        _workoutSession = WorkoutSession(workoutTemplate, programTemplateId, prev)

        _previousSession.value = prev
        _state.value = WorkoutSessionState.Ready
    }


    fun start() {
        workoutSession.start()
        _state.value = WorkoutSessionState.Started
    }


    fun getCurrentItem(): WorkoutSessionItem {
        return workoutSession.getCurrentItem()
    }


    fun hasNextItem(): Boolean {
        return workoutSession.hasNextItem()
    }


    fun getNextItemHint(): String {
        return workoutSession.getNextItemHint()
    }


    fun getNextItemType(): WorkoutSessionItemType? {
        return workoutSession.getNextItemType()
    }


    fun proceed() {
        workoutSession.proceed()
    }


    fun finish() {
        workoutSession.finish()
        _state.value = WorkoutSessionState.Finished
    }


    fun getDuration(): Long {
        return workoutSession.getDuration()
    }


    fun getResults(): List<WorkoutSessionItem.ExerciseSession> {
        return workoutSession.getResults()
    }


    suspend fun saveSession(notes: String?) {
        workoutSession.notes = notes?: ""

        val (workoutSessionRecord, exerciseSessionRecords) = workoutSession.finalize()
        if (exerciseSessionRecords.isNotEmpty()) {
            sessionRepository.addWorkoutSessionRecord(workoutSessionRecord)
            exerciseSessionRecords.forEach {
                sessionRepository.addExerciseSessionRecord(it)
            }
        }

        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
    }
}


class LiveSessionViewModelFactory(private val sessionRepository: SessionsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiveSessionViewModel(sessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}