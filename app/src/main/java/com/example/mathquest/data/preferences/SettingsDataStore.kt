
package com.example.mathquest.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mathquest.model.Difficulty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Creates a shared DataStore for application preferences.
private val Context.settingsDataStore by preferencesDataStore(
    name = "mathquest_settings"
)

// Represents the learner's preferred application settings.
data class UserSettings(
    val difficulty: Difficulty = Difficulty.Easy,
    val highContrast: Boolean = false,
    val largeText: Boolean = false
)

// Manages persistent user preferences.
class SettingsDataStore(context: Context) {

    private val dataStore =
        context.applicationContext.settingsDataStore

    private companion object {

        val DIFFICULTY =
            stringPreferencesKey("difficulty")

        val HIGH_CONTRAST =
            booleanPreferencesKey("high_contrast")

        val LARGE_TEXT =
            booleanPreferencesKey("large_text")
    }

    // Observes settings and provides safe default values.
    val settings: Flow<UserSettings> =
        dataStore.data.catch { exception ->

            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }

        }.map { preferences ->

            val difficultyName =
                preferences[DIFFICULTY]

            val difficulty =
                Difficulty.entries.firstOrNull {
                    it.name == difficultyName
                } ?: Difficulty.Easy

            UserSettings(
                difficulty = difficulty,
                highContrast =
                    preferences[HIGH_CONTRAST] ?: false,
                largeText =
                    preferences[LARGE_TEXT] ?: false
            )
        }

    // Saves the selected learning difficulty.
    suspend fun setDifficulty(difficulty: Difficulty) {

        dataStore.edit { preferences ->
            preferences[DIFFICULTY] = difficulty.name
        }
    }

    // Saves the user's high contrast preference.
    suspend fun setHighContrast(enabled: Boolean) {

        dataStore.edit { preferences ->
            preferences[HIGH_CONTRAST] = enabled
        }
    }

    // Saves the user's large text preference.
    suspend fun setLargeText(enabled: Boolean) {

        dataStore.edit { preferences ->
            preferences[LARGE_TEXT] = enabled
        }
    }

    // Restores the default application preferences.
    suspend fun resetSettings() {

        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
