package unit

import cli.Post
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class ListPostTest {
    private val cmd = CommandLine(Post(InMemoryRepository()))

    @Test
    fun `should list post created`() {
        cmd.execute("-c", "this is my first post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
        """.trimIndent(), result)
    }

    @Test
    fun `should list posts created`() {
        cmd.execute("-c", "this is my first post")
        cmd.execute("-c", "this is my second post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
            2. this is my second post
        """.trimIndent(), result)
    }

    @Test
    fun `should list post with three dots if it is greater than 50 chars`() {
        cmd.execute("-c", "caracters, our online editor can help you to improve word choice and writing style, and, optionally, help you to detect grammar mistakes and plagiarism. To check word count, simply 1")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. caracters, our online editor can help you to impro...
        """.trimIndent(), result)
    }
}
