package com.example.gymbud.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.gymbud.data.ItemIdentifierGenerator
import com.example.gymbud.model.ItemIdentifier
import com.example.gymbud.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

//private const val TAG = "AppRepository"
private const val APP_STATE_NAME = "app_state"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as receiver.
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = APP_STATE_NAME)


class AppRepository(private val context: Context) {
    // the last used ItemIdentifier in the app
    private val lastItemIdentifierKey = longPreferencesKey("last_item_identifier")

    // if a program is selected, active_program_day holds the program day number in that program
    // otherwise, active_program_day holds the id of a workout
    // todo kinda dirty, what if ItemIdentifier wouldn't hold a pos(Int)?
    private val activeProgramIdKey = longPreferencesKey("active_program_id")
    private val activeProgramDayKey = longPreferencesKey("active_program_day")

    private val weightUnitKey = stringPreferencesKey("weight_unit")


    suspend fun reset() {
        updateLastUsedItemIdentifier(ItemIdentifierGenerator.NO_ID)
        ItemIdentifierGenerator.reset() // todo is this ok here.. or where to put it?

        updateActiveProgramAndProgramDay(ItemIdentifierGenerator.NO_ID, ItemIdentifierGenerator.NO_ID)
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


    val activeProgramDay: Flow<Long> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[activeProgramDayKey] ?: ItemIdentifierGenerator.NO_ID
        }


    val activeProgramAndProgramDay: Flow<Pair<ItemIdentifier,Long>> = context.dataStore.data
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

            Pair(program, workout)
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
        }
    }


    suspend fun updateActiveProgramAndProgramDay(programId: ItemIdentifier, programDayIdOrPos: Long) {
        context.dataStore.edit { preferences ->
            preferences[activeProgramIdKey] = programId
            preferences[activeProgramDayKey] = programDayIdOrPos
        }
    }


    suspend fun updateWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[weightUnitKey] = unit.toString()
        }
    }
}