package adapters.inbound.rest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HomeControllerTest {
    @Test
    fun `should list the available resources`() {
        val home = HomeController().home()

        assertEquals("Social Publisher API", home.name)
        assertEquals("/api/posts", home.resources["posts"])
        assertEquals("/api/schedules", home.resources["schedules"])
        assertEquals("/api/poster/run", home.resources["poster"])
        assertEquals("/api/configuration", home.resources["configuration"])
    }
}
