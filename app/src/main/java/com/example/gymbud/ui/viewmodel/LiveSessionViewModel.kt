package com.example.gymbud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.data.repository.SessionsRepository
import com.example.gymbud.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "LiveSessionViewModel"


class LiveSessionViewModel(
    private val sessionRepository: SessionsRepository
): ViewModel() {

    private val _state: MutableStateFlow<WorkoutSessionState> = MutableStateFlow(WorkoutSessionState.NotReady)
    val state: Flow<WorkoutSessionState> = _state

    private val _previousSession: MutableStateFlow<WorkoutSession?> = MutableStateFlow(null)
    val previousSession: Flow<WorkoutSession?> = _previousSession

    private var _workoutSession: WorkoutSession? = null
    private val workoutSession get() = _workoutSession!!

    private var restTimerStartTime: Long = -1


    suspend fun prepare(workoutTemplate: WorkoutTemplate, programTemplateId: ItemIdentifier)  {
        val prev = sessionRepository.getPreviousWorkoutSession(workoutTemplate.id)
        _workoutSession = WorkoutSession(workoutTemplate, programTemplateId, prev)

        _previousSession.value = prev
        _state.value = WorkoutSessionState.Ready
    }


    suspend fun restore(partialRecord: PartialWorkoutSessionRecord): Boolean {
        // retrieve partial session
        val partialSession = sessionRepository.getWorkoutSession(partialRecord.workoutSessionId)

        if (partialSession == null) {
            Log.e(TAG, "No partial session with id $partialRecord.workoutSessionId found on record")
            return false
        }

        // remove partial session from (must do this before we retrieve previous session history)
        sessionRepository.removeSession(partialRecord.workoutSessionId)

        // prepare
        prepare(partialSession.workoutTemplate, partialSession.programTemplateId)

        // bring workout session to current item from partial session
        workoutSession.restart(partialSession, partialRecord.atItem)
        updateRestTimerStartTime(partialRecord.restTimerStartMs)
        _state.value = WorkoutSessionState.Started

        return true
    }


    fun cancel() {
        _previousSession.value = null
        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
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


    fun getCurrentItemType(): WorkoutSessionItemType {
        return workoutSession.getCurrentItem().type
    }


    fun getCurrentItemIndex(): Int {
        return workoutSession.getCurrentItemIndex()
    }


    fun updateRestTimerStartTime(startTime: Long) {
        restTimerStartTime = startTime
    }


    fun getRestTimerStartTime(): Long {
        return restTimerStartTime
    }


    fun proceed() {
        updateRestTimerStartTime(-1)
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


    /**
     * returns the id of the WorkoutSession if it was saved to persistent storage, otherwise returns ItemIdentifier.NO_ID
     */
    suspend fun saveSession(notes: String?): ItemIdentifier {
        var workoutSessionRecordId = ItemIdentifierGenerator.NO_ID

        workoutSession.notes = notes?: ""
        val (workoutSessionRecord, exerciseSessionRecords) = workoutSession.finalize()

        // don't save empty workout sessions
        if (exerciseSessionRecords.isNotEmpty()) {
            workoutSessionRecordId = workoutSessionRecord.id

            sessionRepository.addWorkoutSessionRecord(workoutSessionRecord)

            exerciseSessionRecords.forEach {
                sessionRepository.addExerciseSessionRecord(it)
            }
        }

        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady

        return workoutSessionRecordId
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