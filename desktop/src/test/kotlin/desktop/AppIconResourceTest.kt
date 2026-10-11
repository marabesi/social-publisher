package desktop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppIconResourceTest {
    @Test
    fun `bundled icon decodes as a 512 by 512 png`() {
        val icon = requireNotNull(loadAwtAppIcon())

        assertEquals(512, icon.getWidth(null))
        assertEquals(512, icon.getHeight(null))
    }
}
