package powerrangers.eivom.ui.utility

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val contentDescription: String
)
