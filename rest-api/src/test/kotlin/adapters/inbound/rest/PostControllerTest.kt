package adapters.inbound.rest

import MockedOutput
import adapters.inbound.rest.dto.CreatePostRequest
import adapters.outbound.inmemory.InMemoryPostRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostControllerTest {
    private val postsRepository = InMemoryPostRepository()
    private val controller = PostController(postsRepository, MockedOutput())

    @Test
    fun `should create and list posts`() {
        val message = controller.create(CreatePostRequest("hello from rest"))

        assertEquals("Post has been created", message.message)

        val posts = controller.list()

        assertEquals(1, posts.size)
        assertEquals("hello from rest", posts[0].text)
    }

    @Test
    fun `should reject empty posts`() {
        val message = controller.create(CreatePostRequest(""))

        assertEquals("Missing required fields", message.message)
    }
}
