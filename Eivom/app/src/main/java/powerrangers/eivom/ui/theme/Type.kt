package powerrangers.eivom.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import powerrangers.eivom.R

val Lobster = FontFamily(
    Font(R.font.lobster_regular)
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
        fontFamily = Devil,
        fontSize = 60.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 1.sp,
    ),
    body1 = TextStyle(
        fontFamily = Margot,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h2 = TextStyle (
        fontFamily =  Lobster,
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