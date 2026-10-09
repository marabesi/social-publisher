package adapters.inbound.rest

import MockedOutput
import adapters.inbound.rest.dto.SchedulePostRequest
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SchedulerControllerTest {
    private val postsRepository = InMemoryPostRepository()
    private val schedulerRepository = InMemorySchedulerRepository()
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val controller =
        SchedulerController(
            postsRepository,
            schedulerRepository,
            configurationRepository,
            MockedOutput(),
        )

    @BeforeEach
    fun setUp() {
        configurationRepository.save(SocialConfiguration(timezone = "UTC"))
    }

    @Test
    fun `should schedule a post`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "release notes")))

        val message = controller.create(SchedulePostRequest("1", "2022-07-10T09:00:00Z"))

        assertEquals("Post has been scheduled using UTC timezone", message.message)
    }

    @Test
    fun `should schedule a post for the selected social media`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "release notes")))

        controller.create(SchedulePostRequest("1", "2022-07-10T09:00:00Z", SocialMedia.TWITTER))

        assertEquals(SocialMedia.TWITTER, schedulerRepository.findAll()[0].socialMedia)
    }

    @Test
    fun `should convert a local publish date to utc using the configured timezone`() {
        configurationRepository.save(SocialConfiguration(timezone = "Europe/Madrid"))
        postsRepository.save(arrayListOf(SocialPosts(text = "release notes")))

        val message = controller.create(SchedulePostRequest("1", "2022-10-02T09:00:00"))

        assertEquals("Post has been scheduled using Europe/Madrid timezone", message.message)
        assertEquals("2022-10-02T07:00:00Z", schedulerRepository.findAll()[0].publishDate.toString())
    }

    @Test
    fun `should filter and order schedules by query parameters`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "release notes"),
                SocialPosts(text = "draft"),
            ),
        )
        controller.create(SchedulePostRequest("1", "2022-07-10T09:00:00Z"))
        controller.create(SchedulePostRequest("2", "2023-01-10T09:00:00Z"))

        val filtered = controller.list(null, null, "post.text=release notes", null)
        assertEquals(1, filtered.size)
        assertEquals("1", filtered[0].postId)

        val ordered = controller.list(null, null, null, "publish_date=desc")
        assertEquals("2", ordered[0].postId)
        assertEquals("1", ordered[1].postId)
    }

    @Test
    fun `should delete a schedule`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "release notes")))
        controller.create(SchedulePostRequest("1", "2022-07-10T09:00:00Z"))

        val message = controller.delete("1")

        assertEquals("Schedule 1 has been removed from post 1", message.message)
    }
}
