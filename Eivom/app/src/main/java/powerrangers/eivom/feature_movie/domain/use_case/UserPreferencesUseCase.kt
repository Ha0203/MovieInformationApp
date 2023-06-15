package powerrangers.eivom.feature_movie.domain.use_case

import androidx.compose.ui.graphics.Color
import powerrangers.eivom.feature_movie.data.utility.UserPreferencesKey
import powerrangers.eivom.feature_movie.domain.repository.UserPreferencesRepository
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import java.time.format.DateTimeFormatter

// If adding use case -> adding use case in app module too
// Use case for user preferences
data class UserPreferencesUseCase(
    // Save use case
    val saveBackgroundColor: SaveBackgroundColor,
    val saveDateFormat: SaveDateFormat,

    // Get use case
    val getBackgroundColor: GetBackgroundColor,
    val getDateFormat: GetDateFormat
)

// Save use case
class SaveBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(backgroundColor: Long) {
        // Check if backgroundColor is valid or not to throw exception
        userPreferencesRepository.saveBackgroundColor(backgroundColor = backgroundColor)
    }
}

class SaveDateFormat(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(dateFormat: String) {
        val correctDate = correctDateFormat(dateFormat = dateFormat)
        if (!checkDateFormat(correctDate)) {
            TODO("Throw exception")
        }
        userPreferencesRepository.saveDateFormat(dateFormat = correctDate)
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
            DateTimeFormatter.ofPattern(dateFormat)
            true
        } catch (error: Throwable) {
            false
        }
    }
}

// Get use case
class GetBackgroundColor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): Color =
        Color(
            (userPreferencesRepository.getUserPreferences(key = UserPreferencesKey.BACKGROUND_COLOR) as? Number)
                ?.toLong()
                ?: DefaultValue.BACKGROUND_COLOR
        )
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