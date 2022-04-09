package unit

import Post
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class ListPostTest {

    @Test
    fun `should list posts created`() {
        val cmd = CommandLine(Post(InMemoryRepository()))

        cmd.execute("-c", "this is my first post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
        """.trimIndent(), result)
    }
}