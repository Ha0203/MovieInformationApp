package powerrangers.eivom.domain.repository

interface UserPreferencesRepository {
    // Save functions
    // Color mode
    suspend fun saveColorMode(isCustom: Boolean)

    // Custom color
    suspend fun saveTopbarBackgroundColor(backgroundColor: Long)
    suspend fun saveSidebarBackgroundColor(backgroundColor: Long)
    suspend fun saveScreenBackgroundColor(backgroundColor: Long)
    suspend fun saveMovieNoteBackgroundColor(backgroundColor: Long)
    suspend fun saveDialogBackgroundColor(backgroundColor: Long)
    suspend fun saveTopbarTextColor(textColor: Long)
    suspend fun saveSidebarTextColor(textColor: Long)
    suspend fun saveScreenTextColor(textColor: Long)
    suspend fun saveMovieNoteTextColor(textColor: Long)
    suspend fun saveDialogTextColor(textColor: Long)

    // Display
    suspend fun saveOriginalTitleDisplay(isDisplay: Boolean)
    suspend fun saveDateFormat(dateFormat: String)

    // Notification
    suspend fun saveNotificationBeforeMonth(isNotification: Boolean)
    suspend fun saveNotificationBeforeWeek(isNotification: Boolean)
    suspend fun saveNotificationBeforeDay(isNotification: Boolean)
    suspend fun saveNotificationOnDate(isNotification: Boolean)

    // Get function
    suspend fun getUserPreferences(key: String): Any?
}