package powerrangers.eivom.ui.utility

import androidx.compose.ui.graphics.Color
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import java.time.format.DateTimeFormatter

data class UserPreferences(
    // Color mode
    val colorMode: Boolean = DefaultValue.COLOR_MODE,
    // Custom color
    val topbarBackgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val sidebarBackgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val screenBackgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val movieNoteBackgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val dialogBackgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val topbarTextColor: Color = Color(DefaultValue.TEXT_COLOR),
    val sidebarTextColor: Color = Color(DefaultValue.TEXT_COLOR),
    val screenTextColor: Color = Color(DefaultValue.TEXT_COLOR),
    val movieNoteTextColor: Color = Color(DefaultValue.TEXT_COLOR),
    val dialogTextColor: Color = Color(DefaultValue.TEXT_COLOR),
    // Display
    val originalTitleDisplay: Boolean = DefaultValue.ORIGINAL_TITLE_DISPLAY,
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT),
    // Notification
    val notificationBeforeMonth: Boolean = DefaultValue.IS_NOTIFICATION,
    val notificationBeforeWeek: Boolean = DefaultValue.IS_NOTIFICATION,
    val notificationBeforeDay: Boolean = DefaultValue.IS_NOTIFICATION,
    val notificationOnDate: Boolean = DefaultValue.IS_NOTIFICATION
)
