package unit

import MockedOutput
import adapters.inbound.cli.Scheduler
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialPosts
import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant

class SchedulerTest {
    private lateinit var app: Scheduler
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = Scheduler(InMemoryPostRepository(), InMemorySchedulerRepository(), MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @CsvSource("""
        1,''
        1, 2020-12-23T11:11:00Z
        '', 2020-12-23T11:11:00Z
    """)
    @ParameterizedTest
    fun `should show friendly message on empty date`(postId: String?, targetDate: String?) {
        cmd.execute("-p", postId, "-d", targetDate)
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @Test
    fun `should show help message for schedule command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("scheduler", "--help")
        assertEquals("""
            Usage: social scheduler [-chlV] [-d=<targetDate>] [-p=<postId>]
              -c                 Sets the cli to schedule a post
              -d=<targetDate>    Target date
              -h, --help         Show this help message and exit.
              -l                 List scheduled posts
              -p=<postId>        Post id
              -V, --version      Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "2"])
    fun `should show message if post id does not exists`(id: String) {
        cmd.execute("-c", "-p", id, "-d", "2022-10-02 at 09:00 PM")
        assertEquals("Couldn't find post with id $id", cmd.getExecutionResult())
    }

    @Test
    fun `should schedule post to be published`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput())
        val cmd = CommandLine(app)

        cmd.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post has been scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should list post with id 1 to be scheduled`() {
        val postsRepository = InMemoryPostRepository()
        val post = SocialPosts(text = "anything")
        postsRepository.save(arrayListOf(
            post
        ))

        val scheduleRepository = InMemorySchedulerRepository()
        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-02T09:00:00Z")
            )
        )

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput())
        val cmd = CommandLine(app)
        cmd.execute("-l")

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
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

        val scheduleRepository = InMemorySchedulerRepository()
        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2022-10-02T09:00:00Z")
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post2, Instant.parse("2022-11-02T10:00:00Z")
            ),
        )

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput())
        val cmd = CommandLine(app)
        cmd.execute("-l")

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 2 will be published on 2022-11-02T10:00:00Z
        """.trimIndent(), cmd.getExecutionResult())
    }
}