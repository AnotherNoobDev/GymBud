package com.gymbud.gymbud.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.gymbud.gymbud.data.ItemIdentifierGenerator
import com.gymbud.gymbud.model.ItemIdentifier
import com.gymbud.gymbud.model.PartialWorkoutSessionRecord
import com.gymbud.gymbud.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

//private const val TAG = "AppRepository"
private const val APP_STATE_NAME = "app_state"

private const val DEFAULT_DAILY_WORKOUT_REMINDER_TIME = 60L * 10 // 10:00AM

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as receiver.
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = APP_STATE_NAME)


class AppRepository(private val context: Context) {
    // app started for the first time
    private val firstTimeStartKey = booleanPreferencesKey("first_time_start")

    // the last used ItemIdentifier in the app
    private val lastItemIdentifierKey = longPreferencesKey("last_item_identifier")

    // if a program is selected, active_program_day holds the program day number in that program
    // otherwise, active_program_day holds the id of a workout
    // todo kinda dirty, what if ItemIdentifier wouldn't hold a pos(Int)?
    private val activeProgramIdKey = longPreferencesKey("active_program_id")
    private val activeProgramDayKey = longPreferencesKey("active_program_day")
    private val activeProgramDayLastUpdateTimestamp = longPreferencesKey("active_program_day_last_update_timestamp")

    private val weightUnitKey = stringPreferencesKey("weight_unit")

    private val liveSessionKeepScreenOnKey = booleanPreferencesKey("live_session_keep_screen_on")

    private val partialWorkoutSessionIdKey = longPreferencesKey("partial_workout_session_id")
    private val partialWorkoutSessionAtItemKey = intPreferencesKey("partial_workout_session_at_item")
    private val partialWorkoutSessionProgressedToItemKey = intPreferencesKey("partial_workout_session_progressed_to_item")
    private val partialWorkoutSessionStartTime = longPreferencesKey("partial_workout_session_start_time")
    private val partialWorkoutSessionRestTimerStart = longPreferencesKey("partial_workout_session_rest_timer_start")

    private val useDarkThemeKey = booleanPreferencesKey("use_dark_theme")

    private val dailyWorkoutReminderKey = booleanPreferencesKey("daily_workout_reminder")
    private val dailyWorkoutReminderTimeKey = longPreferencesKey("daily_workout_reminder_time") // as minutes from 00:00


    suspend fun reset() {
        updateFirstTimeStart(true)

        updateLastUsedItemIdentifier(ItemIdentifierGenerator.NO_ID)
        ItemIdentifierGenerator.reset() // todo is this ok here.. or where to put it?

        updateActiveProgramAndProgramDay(ItemIdentifierGenerator.NO_ID, ItemIdentifierGenerator.NO_ID)

        clearPartialWorkoutSessionInfo()
    }


    val lastItemIdentifier: Flow<ItemIdentifier> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[lastItemIdentifierKey] ?: ItemIdentifierGenerator.NO_ID
        }


    val activeProgramId: Flow<ItemIdentifier> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[activeProgramIdKey] ?: ItemIdentifierGenerator.NO_ID
        }


    val activeProgramDay: Flow<Pair<Long, Long>> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val workout = preferences[activeProgramDayKey] ?: ItemIdentifierGenerator.NO_ID
            val workoutTimestamp = preferences[activeProgramDayLastUpdateTimestamp] ?: 0

            Pair(workout,workoutTimestamp)
        }


    val activeProgramAndProgramDay: Flow<Triple<ItemIdentifier,Long, Long>> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val program = preferences[activeProgramIdKey] ?: ItemIdentifierGenerator.NO_ID
            val workout = preferences[activeProgramDayKey] ?: ItemIdentifierGenerator.NO_ID
            val workoutTimestamp = preferences[activeProgramDayLastUpdateTimestamp] ?: 0

            Triple(program, workout, workoutTimestamp)
        }


    val weightUnit: Flow<WeightUnit> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            WeightUnit.valueOf(preferences[weightUnitKey]?: "KG")
        }


    val liveSessionKeepScreenOn: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[liveSessionKeepScreenOnKey]?: true
        }


    val partialWorkoutSessionRecord: Flow<PartialWorkoutSessionRecord> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            PartialWorkoutSessionRecord(
                preferences[partialWorkoutSessionIdKey] ?: ItemIdentifierGenerator.NO_ID,
                preferences[partialWorkoutSessionAtItemKey] ?: -1,
                preferences[partialWorkoutSessionProgressedToItemKey] ?: -1,
                preferences[partialWorkoutSessionStartTime] ?: 0,
                preferences[partialWorkoutSessionRestTimerStart]?: 0
            )
        }


    val firstTimeStart: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[firstTimeStartKey]?: true
        }


    val useDarkTheme: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[useDarkThemeKey]?: true
        }


    val dailyWorkoutReminder: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[dailyWorkoutReminderKey]?: true
        }


    val dailyWorkoutReminderTime: Flow<Long> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[dailyWorkoutReminderTimeKey]?: DEFAULT_DAILY_WORKOUT_REMINDER_TIME
        }


    suspend fun updateLastUsedItemIdentifier(id: ItemIdentifier) {
        context.dataStore.edit { preferences ->
            preferences[lastItemIdentifierKey] = id
        }
    }


    suspend fun updateActiveProgram(id: ItemIdentifier) {
        context.dataStore.edit { preferences ->
            preferences[activeProgramIdKey] = id
        }
    }


    suspend fun updateActiveProgramDay(idOrPos: Long) {
        context.dataStore.edit { preferences ->
            preferences[activeProgramDayKey] = idOrPos
            preferences[activeProgramDayLastUpdateTimestamp] = System.currentTimeMillis()
        }
    }


    suspend fun updateActiveProgramAndProgramDay(programId: ItemIdentifier, programDayIdOrPos: Long) {
        context.dataStore.edit { preferences ->
            preferences[activeProgramIdKey] = programId
            preferences[activeProgramDayKey] = programDayIdOrPos
            preferences[activeProgramDayLastUpdateTimestamp] = System.currentTimeMillis()
        }
    }


    suspend fun updateWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[weightUnitKey] = unit.toString()
        }
    }


    suspend fun updateLiveSessionKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[liveSessionKeepScreenOnKey] = enabled
        }
    }


    suspend fun savePartialWorkoutSessionInfo(session: PartialWorkoutSessionRecord) {
        context.dataStore.edit { preferences ->
            preferences[partialWorkoutSessionIdKey] = session.workoutSessionId
            preferences[partialWorkoutSessionAtItemKey]= session.atItem
            preferences[partialWorkoutSessionProgressedToItemKey] = session.progressedToItem
            preferences[partialWorkoutSessionStartTime] = session.startTimeMs
            preferences[partialWorkoutSessionRestTimerStart] = session.restTimerStartMs
        }
    }


    suspend fun clearPartialWorkoutSessionInfo() {
        context.dataStore.edit { preferences ->
            preferences[partialWorkoutSessionIdKey] = ItemIdentifierGenerator.NO_ID
            preferences[partialWorkoutSessionAtItemKey]= -1
            preferences[partialWorkoutSessionProgressedToItemKey] = -1
            preferences[partialWorkoutSessionStartTime] = 0
            preferences[partialWorkoutSessionRestTimerStart] = 0
        }
    }


    suspend fun updateFirstTimeStart(isFirstTimeStart: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[firstTimeStartKey] = isFirstTimeStart
        }
    }


    suspend fun updateUseDarkTheme(use: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[useDarkThemeKey] = use
        }
    }


    suspend fun updateDailyWorkoutReminder(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[dailyWorkoutReminderKey] = enabled
        }
    }


    suspend fun updateDailyWorkoutReminderTime(timeInMinutes: Long) {
        context.dataStore.edit { preferences ->
            preferences[dailyWorkoutReminderTimeKey] = timeInMinutes
        }
    }
}