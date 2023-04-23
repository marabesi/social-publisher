package application.poster

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.SocialThirdParty
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class ExecutorTest {
    private lateinit var executor: Executor
    private lateinit var schedulerRepository: SchedulerRepository
    private lateinit var configurationRepository: ConfigurationRepository
    private var currentDate: Instant = Instant.now()
    private val socialThirdParty: SocialThirdParty = mockk()

    @BeforeEach
    fun setUp() {
        cleanUpCreatedMocks()
        schedulerRepository = InMemorySchedulerRepository()
        configurationRepository = ConfigurationInMemoryRepository()

        buildApplication(currentDate)
    }

    @AfterEach
    fun tearDown() {
        cleanUpCreatedMocks()
    }

    @Test
    fun `should display friendly message if there is no posts to send to twitter`() {
        val result = executor.invoke()

        Assertions.assertEquals("There are no posts to be posted", result)
    }

    @Test
    fun `should send multiple posts to twitter`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        val scheduledItem = ScheduledItem(
            SocialPosts(id = "1", text = "random post text"),
            Instant.parse("2014-12-22T10:15:30Z"),
            "1",
        )

        val scheduledItem2 = ScheduledItem(
            SocialPosts(id = "2", text = "another post"),
            Instant.parse("2014-12-22T09:15:30Z"),
            "2"
        )

        schedulerRepository.save(
            scheduledItem
        )
        schedulerRepository.save(
            scheduledItem2
        )

        every { socialThirdParty.send(scheduledItem) } returns scheduledItem.post
        every { socialThirdParty.send(scheduledItem2) } returns scheduledItem2.post

        val result = executor.invoke()

        Assertions.assertEquals(
            """
            Post 1 sent to twitter
            Post 2 sent to twitter
            """.trimIndent(),
            result
        )
    }

    @Test
    fun `should not post when publish date has not arrived yet`() {
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )

        val instantExpected = "2014-12-22T10:15:31Z"
        val clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))

        currentDate = Instant.now(clock)

        buildApplication(currentDate)

        val result = executor.invoke()

        Assertions.assertEquals(
            "Waiting for the date to come to publish post 1 (scheduled for 22 Dec 2014 10:15:32)",
            result
        )
    }

    @Test
    fun `should not post posts when publish date has not arrived yet`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("2", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )

        buildApplication(currentDate)

        val result = executor.invoke()

        Assertions.assertEquals(
            """
            Waiting for the date to come to publish post 1 (scheduled for 22 Dec 2014 10:15:32)
            Waiting for the date to come to publish post 2 (scheduled for 22 Dec 2014 10:15:32)
            """.trimIndent(),
            result
        )
    }

    @Test
    fun `should not post posts twice`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-21T10:15:32Z")
            )
        )
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("2", "random post text 2"),
                Instant.parse("2014-12-21T10:15:32Z")
            )
        )
        every { socialThirdParty.send(any()) } returns mockk()

        executor.invoke()
        executor.invoke()

        val result = executor.invoke()

        Assertions.assertEquals("There are no posts to be posted", result)
    }

    private fun buildApplication(currentDate: Instant) {
        executor = Executor(
            schedulerRepository,
            MockedOutput(),
            currentDate,
            socialThirdParty,
        )
    }

    private fun cleanUpCreatedMocks() {
        clearMocks(socialThirdParty)
    }
}