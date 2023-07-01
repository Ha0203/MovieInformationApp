package powerrangers.eivom.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import powerrangers.eivom.R

val Rockdale = FontFamily(
    Font(R.font.rockdale)
)
val Margot= FontFamily(
    Font(R.font.margot)
)
val Devil = FontFamily(
    Font(R.font.devilsummonerexpand)
)

// Set of Material typography styles to start with
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = Rockdale,
        fontSize = 50.sp,
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
    )
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