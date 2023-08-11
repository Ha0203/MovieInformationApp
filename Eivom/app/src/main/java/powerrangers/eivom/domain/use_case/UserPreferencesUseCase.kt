package powerrangers.eivom.domain.use_case

import androidx.compose.ui.graphics.Color
import powerrangers.eivom.data.utility.UserPreferencesKey
import powerrangers.eivom.domain.repository.UserPreferencesRepository
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// If adding use case -> adding use case in app module too
// Use case for user preferences
data class UserPreferencesUseCase(
    // Save use case
    // Color mode
    val saveColorMode: SaveColorMode,
    // Custom color
    val saveTopbarBackgroundColor: SaveTopbarBackgroundColor,
    val saveSidebarBackgroundColor: SaveSidebarBackgroundColor,
    val saveScreenBackgroundColor: SaveScreenBackgroundColor,
    val saveMovieNoteBackgroundColor: SaveMovieNoteBackgroundColor,
    val saveDialogBackgroundColor: SaveDialogBackgroundColor,
    val saveTopbarTextColor: SaveTopbarTextColor,
    val saveSidebarTextColor: SaveSidebarTextColor,
    val saveScreenTextColor: SaveScreenTextColor,
    val saveMovieNoteTextColor: SaveMovieNoteTextColor,
    val saveDialogTextColor: SaveDialogTextColor,
    // Display
    val saveOriginalTitleDisplay: SaveOriginalTitleDisplay,
    val saveDateFormat: SaveDateFormat,
    // Notification
    val saveNotificationBeforeMonth: SaveNotificationBeforeMonth,
    val saveNotificationBeforeWeek: SaveNotificationBeforeWeek,
    val saveNotificationBeforeDay: SaveNotificationBeforeDay,
    val saveNotificationOnDate: SaveNotificationOnDate,

    // Get use case
    // Color mode
    val getColorMode: GetColorMode,
    // Custom color
    val getTopbarBackgroundColor: GetTopbarBackgroundColor,
    val getSidebarBackgroundColor: GetSidebarBackgroundColor,
    val getScreenBackgroundColor: GetScreenBackgroundColor,
    val getMovieNoteBackgroundColor: GetMovieNoteBackgroundColor,
    val getDialogBackgroundColor: GetDialogBackgroundColor,
    val getTopbarTextColor: GetTopbarTextColor,
    val getSidebarTextColor: GetSidebarTextColor,
    val getScreenTextColor: GetScreenTextColor,
    val getMovieNoteTextColor: GetMovieNoteTextColor,
    val getDialogTextColor: GetDialogTextColor,
    // Display
    val getOriginalTitleDisplay: GetOriginalTitleDisplay,
    val getDateFormat: GetDateFormat,
    val getDateFormatString: GetDateFormatString,
    // Notification
    val getNotificationBeforeMonth: GetNotificationBeforeMonth,
    val getNotificationBeforeWeek: GetNotificationBeforeWeek,
    val getNotificationBeforeDay: GetNotificationBeforeDay,
    val getNotificationOnDate: GetNotificationOnDate
)

// Save use case
// Color mode
class SaveColorMode(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isCustom: Boolean): Boolean {
        return try {
            userPreferencesRepository.saveColorMode(isCustom = isCustom)
            true
        } catch (e: Exception) {
            false
        }
    }
}

// Custom color
class SaveTopbarBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveTopbarBackgroundColor(backgroundColor = backgroundColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveSidebarBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveSidebarBackgroundColor(backgroundColor = backgroundColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveScreenBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveScreenBackgroundColor(backgroundColor = backgroundColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveMovieNoteBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveMovieNoteBackgroundColor(backgroundColor = backgroundColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveDialogBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveDialogBackgroundColor(backgroundColor = backgroundColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveTopbarTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(textColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveTopbarTextColor(textColor = textColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveSidebarTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(textColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveSidebarTextColor(textColor = textColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveScreenTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(textColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveScreenTextColor(textColor = textColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveMovieNoteTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(textColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveMovieNoteTextColor(textColor = textColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveDialogTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(textColor: Long): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveDialogTextColor(textColor = textColor)
            true
        } catch (e: Exception) {
            false
        }
    }
}

// Display
class SaveOriginalTitleDisplay(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isDisplay: Boolean): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveOriginalTitleDisplay(isDisplay = isDisplay)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveDateFormat(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(dateFormat: String): Boolean {
        val correctDate = correctDateFormat(dateFormat = dateFormat)
        if (!checkDateFormat(correctDate)) {
            return false
        }
        userPreferencesRepository.saveDateFormat(dateFormat = correctDate)
        return true
    }

    private fun correctDateFormat(dateFormat: String): String {
        try {
            val pattern = dateFormat.lowercase()
            var startIndex = -1
            var endIndex = -1
            for (i in pattern.indices) {
                if (pattern[i] == 'm') {
                    if (startIndex < 0)
                        startIndex = i
                    endIndex = i
                } else if (startIndex >= 0)
                    break
            }
            return pattern.substring(0, startIndex) +
                    pattern.substring(startIndex, endIndex + 1).uppercase() +
                    pattern.substring(endIndex + 1)
        } catch (error: Throwable) {
            return dateFormat
        }
    }

    private fun checkDateFormat(dateFormat: String): Boolean {
        return try {
            LocalDate.now().format(DateTimeFormatter.ofPattern(dateFormat))
            true
        } catch (error: Throwable) {
            false
        }
    }
}

// Notification
class SaveNotificationBeforeMonth(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isNotification: Boolean): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveNotificationBeforeMonth(isNotification = isNotification)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveNotificationBeforeWeek(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isNotification: Boolean): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveNotificationBeforeWeek(isNotification = isNotification)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveNotificationBeforeDay(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isNotification: Boolean): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveNotificationBeforeDay(isNotification = isNotification)
            true
        } catch (e: Exception) {
            false
        }
    }
}
class SaveNotificationOnDate(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(isNotification: Boolean): Boolean {
        // Check if backgroundColor is valid or not to throw exception
        return try {
            userPreferencesRepository.saveNotificationOnDate(isNotification = isNotification)
            true
        } catch (e: Exception) {
            false
        }
    }
}

// Get use case
// Color mode
class GetColorMode(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.COLOR_MODE) as? Boolean)
            ?: DefaultValue.COLOR_MODE
}

// Custom color
class GetTopbarBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.TOPBAR_BACKGROUND) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
}
class GetSidebarBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.SIDEBAR_BACKGROUND) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
}
class GetScreenBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.SCREEN_BACKGROUND) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
}
class GetMovieNoteBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.MOVIE_NOTE_BACKGROUND) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
}
class GetDialogBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.DIALOG_BACKGROUND) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
}
class GetTopbarTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.TOPBAR_TEXT) as? Number)
                ?.toLong()
                ?: DefaultValue.TEXT_COLOR
        )
}
class GetSidebarTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.SIDEBAR_TEXT) as? Number)
                ?.toLong()
                ?: DefaultValue.TEXT_COLOR
        )
}
class GetScreenTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.SCREEN_TEXT) as? Number)
                ?.toLong()
                ?: DefaultValue.TEXT_COLOR
        )
}
class GetMovieNoteTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.MOVIE_NOTE_TEXT) as? Number)
                ?.toLong()
                ?: DefaultValue.TEXT_COLOR
        )
}
class GetDialogTextColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.DIALOG_TEXT) as? Number)
                ?.toLong()
                ?: DefaultValue.TEXT_COLOR
        )
}

// Display
class GetOriginalTitleDisplay(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.ORIGINAL_TITLE) as? Boolean)
            ?: DefaultValue.ORIGINAL_TITLE_DISPLAY
}
class GetDateFormat(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): DateTimeFormatter =
        DateTimeFormatter.ofPattern(
            userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.DATE_FORMAT)
                ?.toString()
                ?: DefaultValue.DATE_FORMAT
        )
}

class GetDateFormatString(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): String =
        userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.DATE_FORMAT)
            ?.toString()
            ?: DefaultValue.DATE_FORMAT
}

// Notification
class GetNotificationBeforeMonth(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.NOTIFICATION_BEFORE_MONTH) as? Boolean)
            ?: DefaultValue.IS_NOTIFICATION
}
class GetNotificationBeforeWeek(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.NOTIFICATION_BEFORE_WEEK) as? Boolean)
            ?: DefaultValue.IS_NOTIFICATION
}
class GetNotificationBeforeDay(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.NOTIFICATION_BEFORE_DAY) as? Boolean)
            ?: DefaultValue.IS_NOTIFICATION
}
class GetNotificationOnDate(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Boolean =
        (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.NOTIFICATION_ON_DATE) as? Boolean)
            ?: DefaultValue.IS_NOTIFICATION
}