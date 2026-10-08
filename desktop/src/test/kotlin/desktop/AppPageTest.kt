package desktop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppPageTest {
    @Test
    fun `offers every page in order`() {
        assertEquals(
            listOf("Posts", "Schedules", "Compose", "Poster", "Configuration"),
            AppPage.entries.map { it.title },
        )
    }
}
