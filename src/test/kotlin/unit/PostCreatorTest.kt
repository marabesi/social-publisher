package unit

import Post
import org.junit.jupiter.api.Test
import picocli.CommandLine
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should create post with the desired text`() {
        val cmd = CommandLine(Post())

        cmd.execute("-c", "this is my first post")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            Post has been created
        """.trimIndent(), result)
    }

    @Test
    fun `should list posts created`() {
        val cmd = CommandLine(Post())

        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            No post found
        """.trimIndent(), result)
    }
}