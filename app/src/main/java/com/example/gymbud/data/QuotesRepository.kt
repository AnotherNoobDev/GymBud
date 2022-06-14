package com.example.gymbud.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.io.IOException


private const val ONE_DAY_MS = 24 * 3600 * 1000

private const val QUOTES_STATE_NAME = "quotes_state"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as receiver.
private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = QUOTES_STATE_NAME)

class QuotesRepository(private val context: Context)  {
    private val lastQuoteTimestampKey = longPreferencesKey("last_quote_timestamp")
    private val lastQuoteIndexKey = intPreferencesKey("last_quote_index")

    private val quotes = QuotesDataSource.quotes

    suspend fun getQuoteOfTheDay(): String {
        // get current timestamp
        val now = System.currentTimeMillis()

        // get values from storage
        val preferences = context.dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.first()

        // if no data in storage, we need to generate a new quote
        val lastQuoteIndex = preferences[lastQuoteIndexKey]?: -1
        if (lastQuoteIndex < 0) {
            return generateNewQuoteOfTheDay(now)
        }

        // if time diff >= 24h, we need to generate a new quote
        val lastTimestamp = preferences[lastQuoteTimestampKey]?: 0
        if (now - lastTimestamp >= ONE_DAY_MS) {
            return generateNewQuoteOfTheDay(now)
        }

        // return same quote
        return quotes[lastQuoteIndex]
    }


    private suspend fun generateNewQuoteOfTheDay(timestamp: Long): String {
        // todo we should exhaust all quotes first, just getting a random one for now
        val newQuoteIndex = quotes.indices.random()

        context.dataStore.edit { preferences ->
            preferences[lastQuoteTimestampKey] = timestamp
            preferences[lastQuoteIndexKey] = newQuoteIndex
        }

        return quotes[newQuoteIndex]
    }
}