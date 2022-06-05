package unit

import cli.Post
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class PostCreatorTest {
    private val cmd = CommandLine(Post(InMemoryRepository(), MockedOutput()))

    @Test
    fun `should show friendly message when no arguments is provided to post`() {
        cmd.execute()

        val result = cmd.getExecutionResult<String>()

        assertEquals("Missing required fields", result)
    }

    @Test
    fun `should create post with the desired text`() {
        cmd.execute("-c", "this is my first post")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            Post has been created
        """.trimIndent(), result)
    }

    @Test
    fun `should give a message when no posts exists`() {
        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            No post found
        """.trimIndent(), result)
    }
}