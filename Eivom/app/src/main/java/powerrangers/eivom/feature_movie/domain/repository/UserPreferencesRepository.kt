package powerrangers.eivom.feature_movie.domain.repository

interface UserPreferencesRepository {
    // Save functions
    suspend fun saveBackgroundColor(backgroundColor: Long)
    suspend fun saveDateFormat(dateFormat: String)

    // Get function
    suspend fun getUserPreferences(key: String): Any?
}