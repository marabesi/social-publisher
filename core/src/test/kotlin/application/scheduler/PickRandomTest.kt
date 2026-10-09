package application.scheduler

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.random.Random

class PickRandomTest {
    private lateinit var configurationRepository: ConfigurationInMemoryRepository
    private lateinit var scheduleRepository: InMemorySchedulerRepository
    private lateinit var postsRepository: InMemoryPostRepository

    @BeforeEach
    fun setUp() {
        configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(timezone = "UTC"))
        scheduleRepository = InMemorySchedulerRepository()
        postsRepository = InMemoryPostRepository()
    }

    @Test
    fun `should ask for a date when none is given`() {
        val result = app(now = NOW).invoke("")

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, result)
    }

    @Test
    fun `should reject an invalid date`() {
        val result = app(now = NOW).invoke("2022")

        assertEquals(PickRandom.INVALID_DATE, result)
    }

    @Test
    fun `should schedule the only available post within the given day`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))

        val result = app(now = NOW).invoke("2026-10-07")

        assertTrue(result.startsWith("Post 1 has been randomly scheduled for "), result)
        assertEquals(1, scheduleRepository.findAll().size)
        val publishDate = scheduleRepository.findAll()[0].publishDate
        assertTrue(!publishDate.isBefore(Instant.parse("2026-10-07T00:00:00Z")), publishDate.toString())
        assertTrue(publishDate.isBefore(Instant.parse("2026-10-08T00:00:00Z")), publishDate.toString())
        assertTrue(!publishDate.isBefore(NOW.plusSeconds(30 * 60)), publishDate.toString())
    }

    @Test
    fun `should keep the random slot at least thirty minutes away from now`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))

        app(now = NOW).invoke("2026-10-05")

        val publishDate = scheduleRepository.findAll()[0].publishDate
        assertTrue(!publishDate.isBefore(NOW.plusSeconds(30 * 60)), publishDate.toString())
    }

    @Test
    fun `should report when the given day has no available slot left`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))

        val result = app(now = Instant.parse("2026-10-05T23:45:00Z")).invoke("2026-10-05")

        assertEquals("No available time to schedule a post on 2026-10-05", result)
        assertEquals(0, scheduleRepository.findAll().size)
    }

    @Test
    fun `should not pick a post already scheduled in the same week`() {
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "first"),
                SocialPosts(text = "second"),
            ),
        )
        scheduleRepository.save(ScheduledItem(postsRepository.findById("1")!!, Instant.parse("2026-10-06T09:00:00Z")))

        val result = app(now = NOW).invoke("2026-10-07")

        assertTrue(result.startsWith("Post 2 has been randomly scheduled for "), result)
        assertEquals(2, scheduleRepository.findAll().size)
    }

    @Test
    fun `should pick the post again once the week changes`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))
        scheduleRepository.save(ScheduledItem(postsRepository.findById("1")!!, Instant.parse("2026-10-06T09:00:00Z")))

        val result = app(now = NOW).invoke("2026-10-14")

        assertTrue(result.startsWith("Post 1 has been randomly scheduled for "), result)
    }

    @Test
    fun `should report when every post was already scheduled in the same week`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))
        scheduleRepository.save(ScheduledItem(postsRepository.findById("1")!!, Instant.parse("2026-10-06T09:00:00Z")))

        val result = app(now = NOW).invoke("2026-10-07")

        assertEquals("No post available to schedule on the week of 2026-10-07", result)
    }

    @Test
    fun `should only consider the selected social media when avoiding repeats`() {
        postsRepository.save(arrayListOf(SocialPosts(text = "anything")))
        scheduleRepository.save(
            ScheduledItem(
                postsRepository.findById("1")!!,
                Instant.parse("2026-10-06T09:00:00Z"),
                socialMedia = SocialMedia.LINKEDIN,
            ),
        )

        val result = app(now = NOW).invoke("2026-10-07", SocialMedia.TWITTER)

        assertTrue(result.startsWith("Post 1 has been randomly scheduled for "), result)
        assertEquals(SocialMedia.TWITTER, scheduleRepository.findAll().last().socialMedia)
    }

    private fun app(
        now: Instant,
        random: Random = Random(0),
    ): PickRandom =
        PickRandom(
            postsRepository,
            scheduleRepository,
            configurationRepository,
            MockedOutput(),
            now = { now },
            random = random,
        )

    companion object {
        private val NOW = Instant.parse("2026-10-05T10:00:00Z")
    }
}
