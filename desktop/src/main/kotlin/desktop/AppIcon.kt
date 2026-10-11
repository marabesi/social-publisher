package desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import org.jetbrains.skia.Image
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

const val APP_ICON_RESOURCE = "social-publisher-icon.png"

private const val GLYPH_FRACTION = 0.48f
private const val ART_FRACTION = 0.78f
private const val CORNER_PERCENT = 22
private val ICON_COLORS = listOf(Color(0xFF6366F1), Color(0xFF2563EB))

/** Bundled icon bytes, decoded once and shared by the Compose and AWT loaders. */
private val appIconBytes: ByteArray? by lazy {
    val loader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
    val stream = loader.getResourceAsStream(APP_ICON_RESOURCE) ?: return@lazy null
    stream.use { it.readAllBytes() }
}

/**
 * The application icon, drawn from Compose primitives so it can be regenerated
 * as a PNG with `./gradlew :desktop:generateIcon` instead of being hand-drawn.
 * The art sits inside a transparent margin so the macOS Dock and Alt-Tab
 * switcher display it at the same size as other apps.
 */
@Composable
fun appIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(ART_FRACTION)
                    .clip(RoundedCornerShape(percent = CORNER_PERCENT))
                    .background(Brush.linearGradient(ICON_COLORS)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = rememberVectorPainter(Icons.AutoMirrored.Filled.Send),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.fillMaxSize(GLYPH_FRACTION),
            )
        }
    }
}

/** Loads the generated window icon, or `null` to fall back to the platform default. */
fun loadAppIcon(): Painter? = appIconBytes?.let { BitmapPainter(Image.makeFromEncoded(it).toComposeImageBitmap()) }

/**
 * Loads the icon as an AWT image for the macOS Dock/Alt-Tab switcher and the
 * Windows taskbar, or `null` when the PNG is missing.
 */
fun loadAwtAppIcon(): java.awt.Image? = appIconBytes?.let { ImageIO.read(ByteArrayInputStream(it)) }
