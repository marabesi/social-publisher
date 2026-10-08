package adapters.outbound.csv

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class StorePathTest {
    private val environment = mutableMapOf<String, String>()

    @Test
    fun `defaults to data when the environment variable is not set`() {
        val storePath = StorePath { environment[it] }

        assertEquals("data", storePath.directory())
    }

    @Test
    fun `uses the environment variable when it is set`() {
        environment["SOCIAL_STORE_PATH"] = "/my/path"
        val storePath = StorePath { environment[it] }

        assertEquals("/my/path", storePath.directory())
        assertEquals("/my/path/posts-production.csv", storePath.file("posts-production.csv"))
    }

    @Test
    fun `treats a blank environment variable as unset`() {
        environment["SOCIAL_STORE_PATH"] = "   "
        val storePath = StorePath { environment[it] }

        assertEquals("data", storePath.directory())
    }
}
