package adapters.cli.scheduler

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerList
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialPosts
import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

class SchedulerListTest {
    private lateinit var app: SchedulerList
    private lateinit var cmd: CommandLine
    private val currentTime = Instant.parse("2022-08-13T11:11:00Z")
    private val scheduleRepository = InMemorySchedulerRepository()
    private val postsRepository = InMemoryPostRepository()

    @BeforeEach
    fun setUp() {
        app = SchedulerList(scheduleRepository, MockedOutput(), currentTime)
        cmd = CommandLine(app)
    }

    @Test
    fun `should show message if scheduler is empty`() {
        cmd.execute()
        assertEquals("No posts scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should show help message for scheduler list command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("scheduler", "list",  "--help")
        assertEquals("""
            Usage: social scheduler list [-hV] [--end-date=<endDate>]
                                         [--group-by=<groupBy>] [--start-date=<startDate>]
                  --end-date=<endDate>   list posts until this date
                  --group-by=<groupBy>   Outputs the scheduled posts grouped by a given
                                           criteria
              -h, --help                 Show this help message and exit.
                  --start-date=<startDate>
                                         list posts that has they publish date starting
                                           with this value
              -V, --version              Print version information and exit.

        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should list post with id 1 to be scheduled`() {
        val post = SocialPosts(text = "anything")
        postsRepository.save(arrayListOf(
            post
        ))

        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-02T09:00:00Z")
            )
        )

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should schedule same post twice each on different days`() {
        val post = SocialPosts(text = "first day")
        postsRepository.save(arrayListOf(
            post,
        ))

        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-02T09:00:00Z")
            )
        )
        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-03T09:00:00Z")
            )
        )

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 1 will be published on 2022-10-03T09:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should list posts to be scheduled`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2022-10-02T09:00:00Z")
            )
        )
        scheduleRepository.save(
            ScheduledItem(
                post2, Instant.parse("2022-11-02T10:00:00Z")
            )
        )

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 2 will be published on 2022-11-02T10:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should list posts in the future only`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2021-10-02T09:00:00Z")
            )
        )
        scheduleRepository.save(
            ScheduledItem(
                post2, Instant.parse("2023-11-02T10:00:00Z")
            )
        )

        cmd.execute("--start-date", "2023-11-01T10:00:00Z")

        assertEquals("""
            1. Post with id 2 will be published on 2023-11-02T10:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should list posts until a given date`() {
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2024-01-02T09:00:00Z")
            )
        )
        scheduleRepository.save(
            ScheduledItem(
                post2, Instant.parse("2024-11-02T10:00:00Z")
            )
        )

        cmd.execute("--end-date", "2024-02-02T09:00:00Z")

        assertEquals("""
            1. Post with id 1 will be published on 2024-01-02T09:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should handle attempt to offer invalid date in --end-date`() {
        cmd.execute("--end-date", "123")

        assertEquals("Invalid date in --end-date", cmd.getExecutionResult())
    }

    @Test
    fun `should group list by posts with single post`() {
        val post1 = SocialPosts(text = "anything")
        postsRepository.save(arrayListOf(
            post1,
        ))

        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2021-10-02T09:00:00Z")
            )
        )

        cmd.execute("--group-by", "post")

        assertEquals("""
            1. Post with id 1 posted 1 time(s)
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should group list by posts with multiple post`() {
        val post1 = SocialPosts(id = "1", text = "anything")
        val post2 = SocialPosts(id = "2", text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2021-10-02T09:00:00Z")
            )
        )

        scheduleRepository.save(
            ScheduledItem(
                post2, Instant.parse("2022-11-02T09:00:00Z")
            )
        )

        cmd.execute("--group-by", "post")

        assertEquals("""
            1. Post with id 1 posted 1 time(s)
            2. Post with id 2 posted 1 time(s)
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should validate if entry given for group by is valid`() {
        cmd.execute("--group-by", "whatever")

        assertEquals("Value for group-by is not valid".trimIndent(), cmd.getExecutionResult())
    }
}
