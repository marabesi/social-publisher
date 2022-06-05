package unit

import cli.Post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import persistence.InMemoryRepository
import picocli.CommandLine

class PostCreatorTest {
    private val cmd = CommandLine(Post(InMemoryRepository(), MockedOutput()))

    @Test
    fun `should show friendly message when no arguments is provided to post`() {
        cmd.execute()

        val result = cmd.getExecutionResult<String>()

        assertEquals("Missing required fields", result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["this is my first post", "<b>another</b>"])
    fun `should create post with the desired text`(text: String) {
        cmd.execute("-c", text)
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