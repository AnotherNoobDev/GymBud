package com.gymbud.gymbud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gymbud.gymbud.BuildConfig
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.data.repository.SessionsRepository
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first


//private const val TAG = "LiveSessionViewModel"
private const val PARTIAL_WORKOUT_SESSION_TAG = "partial_workout_session"


class LiveSessionViewModel(
    private val sessionRepository: SessionsRepository,
    private val appRepository: AppRepository
): ViewModel() {

    private val _state: MutableStateFlow<WorkoutSessionState> = MutableStateFlow(WorkoutSessionState.NotReady)
    val state: Flow<WorkoutSessionState> = _state

    private val _previousSession: MutableStateFlow<WorkoutSession?> = MutableStateFlow(null)
    val previousSession: Flow<WorkoutSession?> = _previousSession

    private var _workoutSession: WorkoutSession? = null
    private val workoutSession get() = _workoutSession!!

    private var restTimerStartTime: Long = -1


    suspend fun prepare(workoutTemplate: WorkoutTemplate, programTemplateId: ItemIdentifier, activeSessionId: ItemIdentifier = ItemIdentifierGenerator.NO_ID)  {
        val prev = sessionRepository.getPreviousWorkoutSession(workoutTemplate.id, activeSessionId)
        _workoutSession = WorkoutSession(activeSessionId, workoutTemplate, programTemplateId, prev)

        _previousSession.value = prev
        _state.value = WorkoutSessionState.Ready
    }


    fun cancel() {
        _previousSession.value = null
        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
    }


    // only call once per workout session!
    suspend fun start() {
        val record = workoutSession.start()

        sessionRepository.addWorkoutSessionRecord(record)

        appRepository.savePartialWorkoutSessionInfo(PartialWorkoutSessionRecord(
            workoutSession.getId(),
            getCurrentItemIndex(),
            getProgressedToItemIndex(),
            getStartTime(),
            getRestTimerStartTime()
        ))

        _state.value = WorkoutSessionState.Started
    }


    fun getItems(): List<WorkoutSessionItem> {
        return workoutSession.getItems()
    }


    fun getCurrentItem(): WorkoutSessionItem {
        return workoutSession.getCurrentItem()
    }


    fun hasPreviousItem(): Boolean {
        return workoutSession.hasPreviousItem()
    }


    fun getPreviousItemType(): WorkoutSessionItemType? {
        return workoutSession.getPreviousItemType()
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


    fun getProgressedToItemIndex(): Int {
        return workoutSession.getProgressedToItemIndex()
    }


    suspend fun updateRestTimerStartTime(startTime: Long) {
        restTimerStartTime = startTime

        appRepository.updatePartialWorkoutSessionRestTimerStart(getRestTimerStartTime())
    }


    fun getRestTimerStartTime(): Long {
        return restTimerStartTime
    }


    suspend fun goBack() {
        updateRestTimerStartTime(-1)
        workoutSession.goBack()

        appRepository.updatePartialWorkoutSessionAtItem(getCurrentItemIndex())
    }


    suspend fun proceed() {
        updateRestTimerStartTime(-1)
        workoutSession.proceed()

        appRepository.updatePartialWorkoutSessionAtItem(getCurrentItemIndex())
        appRepository.updatePartialWorkoutSessionProgressedToItem(getProgressedToItemIndex())
    }


    suspend fun resume() {
        updateRestTimerStartTime(-1)
        workoutSession.resume()

        appRepository.updatePartialWorkoutSessionAtItem(getCurrentItemIndex())
    }


    suspend fun goToItem(itemIndex: Int) {
        updateRestTimerStartTime(-1)
        workoutSession.goToItem(itemIndex)

        appRepository.updatePartialWorkoutSessionAtItem(getCurrentItemIndex())
    }


    fun finish() {
        workoutSession.finish()
        _state.value = WorkoutSessionState.Finished
    }


    fun getStartTime(): Long {
        return workoutSession.getStartTime().time
    }


    fun getDuration(): Long {
        return workoutSession.getDuration()
    }


    fun getResults(): List<WorkoutSessionItem.ExerciseSession> {
        return workoutSession.getResults()
    }


    suspend fun completeExerciseSession(reps: Int, resistance: Double, notes: String) {
        val exerciseSession = getCurrentItem() as WorkoutSessionItem.ExerciseSession

        val record = exerciseSession.complete(workoutSession.getId(), reps, resistance, notes)

        if (record != null) {
            if (sessionRepository.hasExerciseSessionRecord(record)) {
                sessionRepository.updateExerciseSessionRecord(record)
            } else {
                sessionRepository.addExerciseSessionRecord(record)
            }
        }
    }


    suspend fun saveSession(notes: String?) {
        workoutSession.notes = notes?: ""
        val workoutSessionRecord = workoutSession.finalize()

        sessionRepository.updateWorkoutSessionRecord(workoutSessionRecord)

        // flag that we are done with session
        appRepository.clearPartialWorkoutSessionInfo()

        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
    }


    suspend fun discardSession() {
        discardPartialSession()

        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
    }


    suspend fun canContinueWorkout(): Boolean {
        val partialWorkoutSessionRecord = appRepository.partialWorkoutSessionRecord.first()

        if (BuildConfig.DEBUG) {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG, "canContinueWorkout: $partialWorkoutSessionRecord")
        }

        return if (partialWorkoutSessionRecord.workoutSessionId == ItemIdentifierGenerator.NO_ID) {
            if (BuildConfig.DEBUG) {
                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"No active workout session session")
            }

            false
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Found active workout session with id: ${partialWorkoutSessionRecord.workoutSessionId}")
            }

            true
        }
    }


    suspend fun restorePartialSession(workout: WorkoutTemplate): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Restoring session started")
        }

        // we are done if no partialWorkoutSession was persisted
        val partialWorkoutSession = appRepository.partialWorkoutSessionRecord.first()

        if (BuildConfig.DEBUG) {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG, "partialWorkoutSession: $partialWorkoutSession")
        }

        if (partialWorkoutSession.workoutSessionId == ItemIdentifierGenerator.NO_ID) {
            if (BuildConfig.DEBUG) {
                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"No session to restore")
            }

            return false
        }

        val restored = restore(partialWorkoutSession, workout)

        if (BuildConfig.DEBUG) {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Restoring session completed")
        }

        return restored
    }


    private suspend fun restore(partialRecord: PartialWorkoutSessionRecord, workout: WorkoutTemplate): Boolean {
        // retrieve partial session
        val partialSession = sessionRepository.getWorkoutSession(partialRecord.workoutSessionId)

        if (partialSession == null) {
            if (BuildConfig.DEBUG) {
                Log.e(PARTIAL_WORKOUT_SESSION_TAG, "No partial session with id ${partialRecord.workoutSessionId} found on record")
            }

            return false
        }

        if (partialSession.workoutTemplate.id != workout.id) {
            if (BuildConfig.DEBUG) {
                Log.e(PARTIAL_WORKOUT_SESSION_TAG, "Partial session is stale.. Found partial session for workout ${partialSession.workoutTemplate.id} but active workout is ${workout.id}")
            }

            return false
        }

        // prepare
        prepare(partialSession.workoutTemplate, partialSession.programTemplateId, partialRecord.workoutSessionId)

        // bring workout session to current item from partial session
        workoutSession.restart(partialSession, partialRecord.atItem, partialRecord.progressedToItem, partialRecord.startTimeMs)
        updateRestTimerStartTime(partialRecord.restTimerStartMs)
        _state.value = WorkoutSessionState.Started

        return true
    }


    suspend fun discardPartialSession() {
        val partialRecord = appRepository.partialWorkoutSessionRecord.first()

        if (partialRecord.workoutSessionId == ItemIdentifierGenerator.NO_ID) {
            return
        }

        sessionRepository.removeSession(partialRecord.workoutSessionId)
        appRepository.clearPartialWorkoutSessionInfo()
    }
}


class LiveSessionViewModelFactory(
    private val sessionRepository: SessionsRepository,
    private val appRepository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveSessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiveSessionViewModel(sessionRepository, appRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}