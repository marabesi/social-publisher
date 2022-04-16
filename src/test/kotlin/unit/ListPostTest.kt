package unit

import Post
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import kotlin.test.assertEquals

class ListPostTest {

    @Test
    fun `should list post created`() {
        val cmd = CommandLine(Post(InMemoryRepository()))

        cmd.execute("-c", "this is my first post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
        """.trimIndent(), result)
    }

    @Test
    fun `should list posts created`() {
        val repository = InMemoryRepository()
        val cmd = CommandLine(Post(repository))

        cmd.execute("-c", "this is my first post")
        cmd.execute("-c", "this is my second post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
            2. this is my second post
        """.trimIndent(), result)
    }
}
