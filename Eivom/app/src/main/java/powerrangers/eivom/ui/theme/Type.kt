package powerrangers.eivom.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import powerrangers.eivom.R

val VintageKing = FontFamily(
    Font(R.font.vintageking)
)

val Margot= FontFamily(
    Font(R.font.margot)
)
val PoppinsBold = FontFamily(
    Font(R.font.poppins_bold)
)
val Poppins = FontFamily(
    Font(R.font.poppins_regular)
)
val LobsterBold = FontFamily(
    Font(R.font.lobstertwo_bold)
)
val LobsterItalic = FontFamily(
    Font(R.font.lobstertwo_italic)
)
val LobsterRegular = FontFamily(
    Font(R.font.lobstertwo_regular)
)
val PoppinsItalic = FontFamily(
    Font(R.font.poppins_italic)
)
val PoppinsMedium = FontFamily(
    Font(R.font.poppins_medium)
)
// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = VintageKing,
        fontWeight = FontWeight.Normal,
        letterSpacing = 1.sp,
    ),
    body1 = TextStyle(
        fontFamily = Margot,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h2 = TextStyle (
        fontFamily =  Margot,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp
    ),
    subtitle1 = TextStyle(
        fontFamily =  PoppinsBold,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp
    ),
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)