package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant

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
            twitterClient = MockedSocialThirdParty(),
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
    fun `should delete a post`() {
        store.createPost("release notes")

        assertEquals("Post 1 has been removed", store.deletePost("1"))
        assertEquals(0, store.posts().size)
    }

    @Test
    fun `should update a post`() {
        store.createPost("release notes")

        assertEquals("Post has been updated", store.updatePost("1", "edited notes"))
        assertEquals("edited notes", store.posts().first().text)
    }

    @Test
    fun `should load the stored configuration`() {
        assertEquals("UTC", store.configuration()?.timezone)
    }

    @Test
    fun `should return no configuration when none is stored`() {
        val emptyStore =
            SocialPublisherStore(
                postsRepository = InMemoryPostRepository(),
                schedulerRepository = InMemorySchedulerRepository(),
                configurationRepository = ConfigurationInMemoryRepository(),
                output = MockedOutput(),
                twitterClient = MockedSocialThirdParty(),
            )

        assertNull(emptyStore.configuration())
    }

    @Test
    fun `should run the poster and publish due schedules`() {
        val posterStore =
            SocialPublisherStore(
                postsRepository = InMemoryPostRepository(),
                schedulerRepository = InMemorySchedulerRepository(),
                configurationRepository =
                    ConfigurationInMemoryRepository()
                        .apply { save(SocialConfiguration(timezone = "UTC")) },
                output = MockedOutput(),
                twitterClient = MockedSocialThirdParty(),
                currentDate = { Instant.parse("2026-10-02T09:00:00Z") },
            )

        posterStore.createPost("release notes")
        posterStore.schedule("1", "2020-01-01T00:00:00Z")

        assertEquals("Post 1 sent to twitter", posterStore.runPoster())
        assertEquals(0, posterStore.schedules().filter { !it.published }.size)
    }

    @Test
    fun `should store a structured configuration`() {
        val configuration = SocialConfiguration(fileName = "prod", storage = "csv", timezone = "Europe/Madrid")

        assertEquals("Configuration has been stored", store.storeConfiguration(configuration))
        assertEquals("Europe/Madrid", store.configuration()?.timezone)
        assertEquals("prod", store.configuration()?.fileName)
    }

    @Test
    fun `should delete a scheduled post`() {
        store.createPost("release notes")
        store.schedule("1", "2099-01-02T09:00:00Z")

        store.deleteSchedule("1")

        assertEquals(0, store.schedules().size)
    }
}
