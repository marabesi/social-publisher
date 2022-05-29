package unit

import Post
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should show friendly message when no arguments is provided to post`() {
        val cmd = CommandLine(Post(InMemoryRepository()))

        cmd.execute()

        val result = cmd.getExecutionResult<String>()

        assertEquals("Missing required fields", result)
    }

    @Test
    fun `should create post with the desired text`() {
        val cmd = CommandLine(Post(InMemoryRepository()))

        cmd.execute("-c", "this is my first post")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            Post has been created
        """.trimIndent(), result)
    }

    @Test
    fun `should give a message when no posts exists`() {
        val cmd = CommandLine(Post(InMemoryRepository()))

        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            No post found
        """.trimIndent(), result)
    }
}