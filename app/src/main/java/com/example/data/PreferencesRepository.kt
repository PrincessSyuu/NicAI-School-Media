package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "novadeck_prefs")

data class UserPreferencesState(
    val isProActive: Boolean = false,
    val isDarkTheme: Boolean = true,
    val dailyPromptsUsed: Int = 0,
    val completedModuleIds: Set<String> = emptySet(),
    val freeDailyPromptLimit: Int = 8
) {
    val promptsRemaining: Int
        get() = (freeDailyPromptLimit - dailyPromptsUsed).coerceAtLeast(0)
}

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val IS_PRO_ACTIVE = booleanPreferencesKey("is_pro_active")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val DAILY_PROMPTS_USED = intPreferencesKey("daily_prompts_used")
        val PROMPT_DATE = stringPreferencesKey("prompt_date")
        val COMPLETED_MODULES = stringSetPreferencesKey("completed_modules")
    }

    private fun todayKey(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        val savedDate = prefs[Keys.PROMPT_DATE] ?: todayKey()
        val used = if (savedDate == todayKey()) (prefs[Keys.DAILY_PROMPTS_USED] ?: 0) else 0
        UserPreferencesState(
            isProActive = prefs[Keys.IS_PRO_ACTIVE] ?: false,
            isDarkTheme = prefs[Keys.IS_DARK_THEME] ?: true,
            dailyPromptsUsed = used,
            completedModuleIds = prefs[Keys.COMPLETED_MODULES] ?: emptySet()
        )
    }

    suspend fun setProActive(active: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_PRO_ACTIVE] = active
        }
    }

    suspend fun setDarkTheme(dark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_DARK_THEME] = dark
        }
    }

    suspend fun incrementDailyPromptUsed() {
        val today = todayKey()
        context.dataStore.edit { prefs ->
            val savedDate = prefs[Keys.PROMPT_DATE]
            val current = if (savedDate == today) (prefs[Keys.DAILY_PROMPTS_USED] ?: 0) else 0
            prefs[Keys.PROMPT_DATE] = today
            prefs[Keys.DAILY_PROMPTS_USED] = current + 1
        }
    }

    suspend fun markModuleCompleted(moduleId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_MODULES] ?: emptySet()
            prefs[Keys.COMPLETED_MODULES] = current + moduleId
        }
    }
}
