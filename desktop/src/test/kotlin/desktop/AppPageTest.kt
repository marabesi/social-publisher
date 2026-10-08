package desktop

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AppPageTest {
    @Test
    fun `offers posts, schedules and compose pages in order`() {
        assertEquals(
            listOf("Posts", "Schedules", "Compose"),
            AppPage.entries.take(3).map { it.title },
        )
    }
}
