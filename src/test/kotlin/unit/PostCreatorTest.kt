package unit

import Post
import PostsRepository
import org.junit.jupiter.api.Test
import picocli.CommandLine
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should create post with the desired text`() {
        val cmd = CommandLine(Post(PostsRepository()))

        cmd.execute("-c", "this is my first post")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            Post has been created
        """.trimIndent(), result)
    }

    @Test
    fun `should give a message when no posts exists`() {
        val cmd = CommandLine(Post(PostsRepository()))

        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            No post found
        """.trimIndent(), result)
    }

    @Test
    fun `should list posts created`() {
        val cmd = CommandLine(Post(PostsRepository()))

        cmd.execute("-c", "this is my first post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
        """.trimIndent(), result)
    }
}