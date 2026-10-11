package desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Renders the application icon from [appIcon] and writes it to the desktop
 * resources so the window can load it at launch.
 *
 * Regenerate it with the dedicated Gradle task:
 *
 * ```
 * ./gradlew :desktop:generateIcon
 * ```
 */
@OptIn(ExperimentalTestApi::class)
class AppIconGeneratorTest {
    @Test
    fun `renders the app icon into the desktop resources`() =
        runDesktopComposeUiTest(width = ICON_SIZE, height = ICON_SIZE) {
            setContent {
                appIcon(Modifier.fillMaxSize())
            }
            waitForIdle()
            saveIcon()
        }

    private fun SkikoComposeUiTest.saveIcon() {
        val data =
            Image
                .makeFromBitmap(captureToImage().asSkiaBitmap())
                .encodeToData(EncodedImageFormat.PNG)
                ?: error("Could not encode the app icon as PNG")
        val file = File(OUTPUT_DIR, APP_ICON_RESOURCE)
        file.parentFile.mkdirs()
        file.writeBytes(data.bytes)
    }

    private companion object {
        const val ICON_SIZE = 512
        val OUTPUT_DIR = File(System.getProperty("icon.dir") ?: "src/main/resources")
    }
}
