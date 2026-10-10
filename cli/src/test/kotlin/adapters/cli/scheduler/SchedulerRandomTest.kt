package adapters.cli.scheduler

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerRandom
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.time.Instant

class SchedulerRandomTest {
    private lateinit var configurationRepository: ConfigurationInMemoryRepository
    private lateinit var scheduleRepository: InMemorySchedulerRepository
    private lateinit var postsRepository: InMemoryPostRepository
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(timezone = "UTC"))
        scheduleRepository = InMemorySchedulerRepository()
        postsRepository = InMemoryPostRepository()
        cmd = CommandLine(app())
    }

    @Test
    fun `should show friendly message when the day is not provided`() {
        cmd.execute()

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, cmd.getExecutionResult())
    }

    @Test
    fun `should reject an invalid day`() {
        cmd.execute("-d", "2022")

        assertEquals("Invalid date time to schedule post", cmd.getExecutionResult())
    }

    @Test
    fun `should schedule the only post for the given day`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))

        cmd.execute("-d", "2026-10-07")

        val result = cmd.getExecutionResult<String>()
        assertTrue(result.startsWith("Post 1 has been randomly scheduled for "), result)
        assertEquals(1, scheduleRepository.findAll().size)
        val publishDate = scheduleRepository.findAll()[0].publishDate
        assertTrue(!publishDate.isBefore(Instant.parse("2026-10-07T00:00:00Z")), publishDate.toString())
        assertTrue(publishDate.isBefore(Instant.parse("2026-10-08T00:00:00Z")), publishDate.toString())
    }

    @Test
    fun `should not create a random post in the past when the day already passed`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))

        cmd.execute("-d", "2026-10-01")

        val result = cmd.getExecutionResult<String>()
        assertTrue(result.startsWith("Post 1 has been randomly scheduled for "), result)
        val publishDate = scheduleRepository.findAll()[0].publishDate
        assertTrue(!publishDate.isBefore(Instant.parse("2026-10-05T10:30:00Z")), publishDate.toString())
    }

    @Test
    fun `should skip posts already scheduled in the same week`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "first"),
                SocialPosts(text = "second"),
            ),
        )
        scheduleRepository.save(ScheduledItem(postsRepository.findById("1")!!, Instant.parse("2026-10-06T09:00:00Z")))

        cmd.execute("-d", "2026-10-07")

        assertTrue(cmd.getExecutionResult<String>().startsWith("Post 2 has been randomly scheduled for "))
    }

    private fun app(): SchedulerRandom =
        SchedulerRandom(
            postsRepository,
            scheduleRepository,
            configurationRepository,
            MockedOutput(),
            Instant.parse("2026-10-05T10:00:00Z"),
        )
}
