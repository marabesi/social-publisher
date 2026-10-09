package desktop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UiScaleTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun `applies the hard coded scale to the provided density`() =
        runComposeUiTest {
            var baseDensity = 0f
            var scaledDensity = 0f

            setContent {
                baseDensity = LocalDensity.current.density
                provideUiScale {
                    scaledDensity = LocalDensity.current.density
                    Box(Modifier.size(1.dp))
                }
            }

            assertEquals(baseDensity * UI_SCALE, scaledDensity, 0.0001f)
        }
}
