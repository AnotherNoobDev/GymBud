package com.gymbud.gymbud.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.data.repository.AppRepository
import com.gymbud.gymbud.data.repository.SessionsRepository
import com.gymbud.gymbud.model.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//private const val TAG = "LiveSessionViewModel"
private const val PARTIAL_WORKOUT_SESSION_TAG = "partial_workout_session"

private enum class RecoveryState {
    Idle,
    RestorationStarted,
    RestorationCompleted,
    PersistenceStarted
}


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

    private var recoveryState =  RecoveryState.Idle

    private val _sessionRestored: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val sessionRestored: Flow<Boolean> = _sessionRestored


    suspend fun prepare(workoutTemplate: WorkoutTemplate, programTemplateId: ItemIdentifier)  {
        val prev = sessionRepository.getPreviousWorkoutSession(workoutTemplate.id)
        _workoutSession = WorkoutSession(workoutTemplate, programTemplateId, prev)

        _previousSession.value = prev
        _state.value = WorkoutSessionState.Ready
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


    fun updateRestTimerStartTime(startTime: Long) {
        restTimerStartTime = startTime
    }


    fun getRestTimerStartTime(): Long {
        return restTimerStartTime
    }


    fun goBack() {
        updateRestTimerStartTime(-1)
        workoutSession.goBack()
    }


    fun proceed() {
        updateRestTimerStartTime(-1)
        workoutSession.proceed()
    }


    fun resume() {
        updateRestTimerStartTime(-1)
        workoutSession.resume()
    }


    fun goToItem(itemIndex: Int) {
        updateRestTimerStartTime(-1)
        workoutSession.goToItem(itemIndex)
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


    /**
     * returns the id of the WorkoutSession if it was saved to persistent storage, otherwise returns ItemIdentifier.NO_ID
     */
    suspend fun saveSession(notes: String?): ItemIdentifier {
        workoutSession.notes = notes?: ""
        val (workoutSessionRecord, exerciseSessionRecords) = workoutSession.finalize()

        val workoutSessionRecordId = workoutSessionRecord.id

        sessionRepository.addWorkoutSessionRecord(workoutSessionRecord)

        exerciseSessionRecords.forEach {
            sessionRepository.addExerciseSessionRecord(it)
        }

        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady

        return workoutSessionRecordId
    }


    fun discardSession() {
        _workoutSession = null
        _state.value = WorkoutSessionState.NotReady
    }


    fun onInterrupt() {
        if (recoveryState != RecoveryState.Idle && recoveryState != RecoveryState.RestorationCompleted) {
            return
        }

        if (_state.value != WorkoutSessionState.Started) {
            return
        }

        _sessionRestored.value = false

        // persist LiveSession
        recoveryState = RecoveryState.PersistenceStarted
        GlobalScope.launch {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session: started")
            // persist partial workout session
            val atItem = getCurrentItemIndex()
            val progressedToItem = getProgressedToItemIndex()
            val startTime = getStartTime()
            val restTimerStartTime = getRestTimerStartTime()

            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session: collected partial session info")

            finish()
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session: finish() completed")

            val workoutSessionId = saveSession("")
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session: saveSession() completed")

            // update app repository with partial workout session id
            appRepository.savePartialWorkoutSessionInfo(
                PartialWorkoutSessionRecord(workoutSessionId, atItem, progressedToItem, startTime, restTimerStartTime)
            )

            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Saving session: completed")
            recoveryState  = RecoveryState.Idle
        }
    }


    fun onRestore() {
        if (recoveryState != RecoveryState.Idle) {
            return
        }

        // restore LiveSession
        recoveryState  = RecoveryState.RestorationStarted
        GlobalScope.launch {
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Restoring session started")
            // we are done if no partialWorkoutSession was persisted
            val partialWorkoutSession = appRepository.partialWorkoutSessionRecord.first()
            Log.d(PARTIAL_WORKOUT_SESSION_TAG, "onResume: $partialWorkoutSession")

            if (partialWorkoutSession.workoutSessionId == ItemIdentifierGenerator.NO_ID) {
                _sessionRestored.value = false
                recoveryState  = RecoveryState.RestorationCompleted
                Log.d(PARTIAL_WORKOUT_SESSION_TAG,"No session to restore")
                return@launch
            }

            val restored = restore(partialWorkoutSession)
            appRepository.clearPartialWorkoutSessionInfo()

            _sessionRestored.value = restored
            Log.d(PARTIAL_WORKOUT_SESSION_TAG,"Restoring session completed")
            recoveryState = RecoveryState.RestorationCompleted
        }
    }


    private suspend fun restore(partialRecord: PartialWorkoutSessionRecord): Boolean {
        // retrieve partial session
        val partialSession = sessionRepository.getWorkoutSession(partialRecord.workoutSessionId)

        if (partialSession == null) {
            Log.e(PARTIAL_WORKOUT_SESSION_TAG, "No partial session with id ${partialRecord.workoutSessionId} found on record")
            return false
        }

        // remove partial session from (must do this before we retrieve previous session history)
        sessionRepository.removeSession(partialRecord.workoutSessionId)

        // prepare
        prepare(partialSession.workoutTemplate, partialSession.programTemplateId)

        // bring workout session to current item from partial session
        workoutSession.restart(partialSession, partialRecord.atItem, partialRecord.progressedToItem, partialRecord.startTimeMs)
        updateRestTimerStartTime(partialRecord.restTimerStartMs)
        _state.value = WorkoutSessionState.Started

        return true
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