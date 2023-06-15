package powerrangers.eivom.feature_movie.ui.utility

import androidx.compose.ui.graphics.Color
import powerrangers.eivom.feature_movie.domain.utility.DefaultValue
import java.time.format.DateTimeFormatter

data class UserPreferences(
    val backgroundColor: Color = Color(DefaultValue.BACKGROUND_COLOR),
    val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern(DefaultValue.DATE_FORMAT)
)
