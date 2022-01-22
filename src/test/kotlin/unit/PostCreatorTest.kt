package unit

import Post
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should create post with the desired text`() {
        val post = Post()
        post.text = "my text"

        assertEquals("""
            Post has been created
        """.trimIndent(), post.call().toString())
    }
}