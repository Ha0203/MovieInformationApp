package powerrangers.eivom.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import powerrangers.eivom.domain.repository.UserPreferencesRepository
import powerrangers.eivom.data.utility.UserPreferencesKey

// Settings Repository
class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {
    private companion object {
        val BACKGROUND_COLOR = longPreferencesKey(UserPreferencesKey.BACKGROUND_COLOR)
        val DATE_FORMAT = stringPreferencesKey(UserPreferencesKey.DATE_FORMAT)
    }

    // Save functions
    override suspend fun saveBackgroundColor(backgroundColor: Long) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR] = backgroundColor
        }
    }
    override suspend fun saveDateFormat(dateFormat: String) {
        dataStore.edit { preferences ->
            preferences[DATE_FORMAT] = dateFormat
        }
    }

    // Get function
    override suspend fun getUserPreferences(key: String): Any? {
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }
}