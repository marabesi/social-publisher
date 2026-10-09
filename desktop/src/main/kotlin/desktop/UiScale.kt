package desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

const val UI_SCALE = 0.85f

@Composable
fun provideUiScale(content: @Composable () -> Unit) {
    val baseDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides
            Density(
                density = baseDensity.density * UI_SCALE,
                fontScale = baseDensity.fontScale,
            ),
        content = content,
    )
}
