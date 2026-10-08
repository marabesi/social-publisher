package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SocialPublisherStoreTest {
    private val postsRepository = InMemoryPostRepository()
    private val schedulerRepository = InMemorySchedulerRepository()
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val store =
        SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = schedulerRepository,
            configurationRepository = configurationRepository,
            output = MockedOutput(),
        )

    @BeforeEach
    fun setUp() {
        configurationRepository.save(SocialConfiguration(timezone = "UTC"))
    }

    @Test
    fun `should create a post and schedule it`() {
        assertEquals("Post has been created", store.createPost("release notes"))
        assertEquals(1, store.posts().size)

        assertEquals("Post has been scheduled using UTC timezone", store.schedule("1", "2099-01-02T09:00:00Z"))
        assertEquals(1, store.schedules().size)
        assertEquals("2099-01-02T09:00:00Z", store.schedules()[0].publishDate.toString())
    }

    @Test
    fun `should delete a scheduled post`() {
        store.createPost("release notes")
        store.schedule("1", "2099-01-02T09:00:00Z")

        store.deleteSchedule("1")

        assertEquals(0, store.schedules().size)
    }
}
