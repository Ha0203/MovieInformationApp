package powerrangers.eivom.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import powerrangers.eivom.data.utility.UserPreferencesKey
import powerrangers.eivom.domain.repository.UserPreferencesRepository

// Settings Repository
class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private companion object {
        // Color mode
        val COLOR_MODE = booleanPreferencesKey(UserPreferencesKey.COLOR_MODE)

        // Custom color
        val TOPBAR_BACKGROUND = longPreferencesKey(UserPreferencesKey.TOPBAR_BACKGROUND)
        val SIDEBAR_BACKGROUND = longPreferencesKey(UserPreferencesKey.SIDEBAR_BACKGROUND)
        val SCREEN_BACKGROUND = longPreferencesKey(UserPreferencesKey.SCREEN_BACKGROUND)
        val MOVIE_NOTE_BACKGROUND = longPreferencesKey(UserPreferencesKey.MOVIE_NOTE_BACKGROUND)
        val DIALOG_BACKGROUND = longPreferencesKey(UserPreferencesKey.DIALOG_BACKGROUND)
        val TOPBAR_TEXT = longPreferencesKey(UserPreferencesKey.TOPBAR_TEXT)
        val SIDEBAR_TEXT = longPreferencesKey(UserPreferencesKey.SIDEBAR_TEXT)
        val SCREEN_TEXT = longPreferencesKey(UserPreferencesKey.SCREEN_TEXT)
        val MOVIE_NOTE_TEXT = longPreferencesKey(UserPreferencesKey.MOVIE_NOTE_TEXT)
        val DIALOG_TEXT = longPreferencesKey(UserPreferencesKey.DIALOG_TEXT)


        // Display
        val ORIGINAL_TITLE = booleanPreferencesKey(UserPreferencesKey.ORIGINAL_TITLE)
        val DATE_FORMAT = stringPreferencesKey(UserPreferencesKey.DATE_FORMAT)

        // Notification
        val NOTIFICATION_BEFORE_MONTH = booleanPreferencesKey(UserPreferencesKey.NOTIFICATION_BEFORE_MONTH)
        val NOTIFICATION_BEFORE_WEEK = booleanPreferencesKey(UserPreferencesKey.NOTIFICATION_BEFORE_WEEK)
        val NOTIFICATION_BEFORE_DAY = booleanPreferencesKey(UserPreferencesKey.NOTIFICATION_BEFORE_DAY)
        val NOTIFICATION_ON_DATE = booleanPreferencesKey(UserPreferencesKey.NOTIFICATION_ON_DATE)
    }

    // Save functions
    // Color mode
    override suspend fun saveColorMode(isCustom: Boolean) {
        dataStore.edit { preferences ->
            preferences[COLOR_MODE] = isCustom
        }
    }

    // Custom color
    override suspend fun saveTopbarBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[TOPBAR_BACKGROUND] = backgroundColor
        }
    }
    override suspend fun saveSidebarBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[SIDEBAR_BACKGROUND] = backgroundColor
        }
    }
    override suspend fun saveScreenBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[SCREEN_BACKGROUND] = backgroundColor
        }
    }
    override suspend fun saveMovieNoteBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[MOVIE_NOTE_BACKGROUND] = backgroundColor
        }
    }
    override suspend fun saveDialogBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[DIALOG_BACKGROUND] = backgroundColor
        }
    }
    override suspend fun saveTopbarTextColor(textColor: Long) {
        dataStore.edit { preferences ->
            preferences[TOPBAR_TEXT] = textColor
        }
    }
    override suspend fun saveSidebarTextColor(textColor: Long) {
        dataStore.edit { preferences ->
            preferences[SIDEBAR_TEXT] = textColor
        }
    }
    override suspend fun saveScreenTextColor(textColor: Long) {
        dataStore.edit { preferences ->
            preferences[SCREEN_TEXT] = textColor
        }
    }
    override suspend fun saveMovieNoteTextColor(textColor: Long) {
        dataStore.edit { preferences ->
            preferences[MOVIE_NOTE_TEXT] = textColor
        }
    }
    override suspend fun saveDialogTextColor(textColor: Long) {
        dataStore.edit { preferences ->
            preferences[DIALOG_TEXT] = textColor
        }
    }

    // Display
    override suspend fun saveOriginalTitleDisplay(isDisplay: Boolean) {
        dataStore.edit { preferences ->
            preferences[ORIGINAL_TITLE] = isDisplay
        }
    }
    override suspend fun saveDateFormat(dateFormat: String) {
        dataStore.edit { preferences ->
            preferences[DATE_FORMAT] = dateFormat
        }
    }

    // Notification
    override suspend fun saveNotificationBeforeMonth(isNotification: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_BEFORE_MONTH] = isNotification
        }
    }
    override suspend fun saveNotificationBeforeWeek(isNotification: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_BEFORE_WEEK] = isNotification
        }
    }
    override suspend fun saveNotificationBeforeDay(isNotification: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_BEFORE_DAY] = isNotification
        }
    }
    override suspend fun saveNotificationOnDate(isNotification: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_ON_DATE] = isNotification
        }
    }

    // Get function
    override suspend fun getUserPreferences(key: String): Any? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}