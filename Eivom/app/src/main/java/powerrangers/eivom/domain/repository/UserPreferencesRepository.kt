package powerrangers.eivom.domain.repository

interface UserPreferencesRepository {
    // Save functions
    suspend fun saveBackgroundColor(backgroundColor: Long)
    suspend fun saveDateFormat(dateFormat: String)

    // Get function
    suspend fun getUserPreferences(key: String): Any?
}