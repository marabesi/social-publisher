package unit

import MockedOutput
import adapters.inbound.cli.Scheduler
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
            Usage: social scheduler list [-hV] [--future-only]
                  --future-only   list posts that are beyond the future date
              -h, --help          Show this help message and exit.
              -V, --version       Print version information and exit.

        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should list post with id 1 to be scheduled`() {
        val postsRepository = InMemoryPostRepository()
        val post = SocialPosts(text = "anything")
        postsRepository.save(arrayListOf(
            post
        ))

        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-02T09:00:00Z")
            )
        )

        val cmdParent = CommandLine(Scheduler(postsRepository, scheduleRepository, MockedOutput()))
        cmdParent.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should schedule same post twice each on different days`() {
        val postsRepository = InMemoryPostRepository()
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

        val cmdParent = CommandLine(Scheduler(postsRepository, scheduleRepository, MockedOutput()))
        cmdParent.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        cmdParent.execute("-c", "-p", "2", "-d", "2022-10-03T09:00:00Z")

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 1 will be published on 2022-10-03T09:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should list posts to be scheduled`() {
        val postsRepository = InMemoryPostRepository()
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        val cmdParent = CommandLine(Scheduler(postsRepository, scheduleRepository, MockedOutput()))
        cmdParent.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        cmdParent.execute("-c", "-p", "2", "-d", "2022-11-02T10:00:00Z")

        cmd.execute()

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 2 will be published on 2022-11-02T10:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }

    @Test
    fun `should list posts in the future only`() {
        val postsRepository = InMemoryPostRepository()
        val post1 = SocialPosts(text = "anything")
        val post2 = SocialPosts(text = "anything-2")
        postsRepository.save(arrayListOf(
            post1,
            post2
        ))

        val cmdParent = CommandLine(Scheduler(postsRepository, scheduleRepository, MockedOutput()))
        cmdParent.execute("-c", "-p", "1", "-d", "2021-10-02T09:00:00Z")
        cmdParent.execute("-c", "-p", "2", "-d", "2023-11-02T10:00:00Z")

        cmd.execute("--future-only")

        assertEquals("""
            1. Post with id 2 will be published on 2023-11-02T10:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }
}
