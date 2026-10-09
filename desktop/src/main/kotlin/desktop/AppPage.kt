package desktop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppPage(
    val title: String,
    val icon: ImageVector,
) {
    POSTS("Posts", Icons.Default.List),
    SCHEDULES("Schedules", Icons.Default.DateRange),
    COMPOSE("Compose", Icons.Default.Edit),
    POSTER("Poster", Icons.Default.PlayArrow),
    CONFIGURATION("Configuration", Icons.Default.Settings),
    HOME("Home", Icons.Default.Home),
}
