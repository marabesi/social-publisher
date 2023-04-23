package application.scheduler

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class SchedulerCreateTest {
    private lateinit var configurationRepository: ConfigurationInMemoryRepository
    private lateinit var scheduleRepository: InMemorySchedulerRepository
    private lateinit var postsRepository: InMemoryPostRepository
    private lateinit var app: Create

    @BeforeEach
    fun setUp() {
        buildApplication(
            SocialConfiguration(timezone = "UTC")
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "2"])
    fun `should show message if post id does not exists`(id: String) {
        val result = app.invoke(id, "2022-10-02 at 09:00 PM")
        assertEquals("Couldn't find post with id $id", result)
    }

    @CsvSource(
        """
        1,''
        1, 2020-12-23T11:11:00Z
        '', 2020-12-23T11:11:00Z
    """
    )
    @ParameterizedTest
    fun `should show friendly message on empty date`(postId: String, targetDate: String) {
        val result = app.invoke(postId, targetDate)
        assertEquals(Messages.MISSING_REQUIRED_FIELDS, result)
    }

    @Test
    fun `should schedule post to be published`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "anything")
            )
        )

        val result = app.invoke("1", "2022-10-02T09:00:00Z")
        assertEquals("Post has been scheduled using UTC timezone", result)
    }

    @Test
    fun `should schedule post to be published using europe madrid as timezone`() {
        buildApplication(SocialConfiguration(timezone = "Europe/Madrid"))
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "anything")
            )
        )

        val result = app.invoke("1", "2022-10-02T09:00:00Z")

        assertEquals("Post has been scheduled using Europe/Madrid timezone", result)
    }

    @Test
    fun `should not schedule post twice on the same date time to be published`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "anything")
            )
        )

        app.invoke("1", "2022-10-02T09:00:00Z")
        val result = app.invoke("1", "2022-10-02T09:00:00Z")

        assertEquals("Post is already scheduled for 2022-10-02T09:00:00Z", result)
    }

    @Test
    fun `should warn when invalid date is given to be published`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "anything")
            )
        )

        val result = app.invoke("1", "2022")

        assertEquals("Invalid date time to schedule post", result)
    }

    private fun buildApplication(configuration: SocialConfiguration) {
        postsRepository = InMemoryPostRepository()
        scheduleRepository = InMemorySchedulerRepository()
        configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(configuration)

        app = Create(
            postsRepository,
            scheduleRepository,
            configurationRepository,
            MockedOutput()
        )
    }
}
