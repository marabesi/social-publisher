package adapters.inbound.rest

import MockedOutput
import adapters.inbound.rest.dto.CreatePostRequest
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialMedia
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class PostControllerTest {
    private val postsRepository = InMemoryPostRepository()
    private val schedulerRepository = InMemorySchedulerRepository()
    private val controller = PostController(postsRepository, schedulerRepository, MockedOutput())

    @Test
    fun `should create and list posts`() {
        val message = controller.create(CreatePostRequest("hello from rest"))

        assertEquals("Post has been created", message.message)

        val posts = controller.list(null, null, null)

        assertEquals(1, posts.size)
        assertEquals("hello from rest", posts[0].text)
        assertEquals(15, posts[0].characterCount)
        assertEquals(280, posts[0].characterLimit)
    }

    @Test
    fun `should reject empty posts`() {
        val message = controller.create(CreatePostRequest(""))

        assertEquals("Missing required fields", message.message)
    }

    @Test
    fun `should filter posts by fuzzy text`() {
        controller.create(CreatePostRequest("hello from rest"))
        controller.create(CreatePostRequest("release notes"))

        val posts = controller.list(search = "hlo rst", ids = null, socialMedia = null)

        assertEquals(1, posts.size)
        assertEquals("hello from rest", posts[0].text)
    }

    @Test
    fun `should filter posts by more than one id`() {
        controller.create(CreatePostRequest("first"))
        controller.create(CreatePostRequest("second"))
        controller.create(CreatePostRequest("third"))

        val posts = controller.list(search = null, ids = "1,3", socialMedia = null)

        assertEquals(listOf("first", "third"), posts.map { it.text })
    }

    @Test
    fun `should filter posts by social media`() {
        controller.create(CreatePostRequest("first"))
        controller.create(CreatePostRequest("second"))
        schedulerRepository.save(
            ScheduledItem(postsRepository.findById("2")!!, Instant.EPOCH, socialMedia = SocialMedia.LINKEDIN),
        )

        val posts = controller.list(search = null, ids = null, socialMedia = SocialMedia.LINKEDIN)

        assertEquals(listOf("second"), posts.map { it.text })
    }
}
