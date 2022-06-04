package unit

import socialPosts.ScheduledItem
import commands.Scheduler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import persistence.InMemorySchedulerRepository
import picocli.CommandLine
import socialPosts.SocialPosts
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import kotlin.test.assertEquals

class PostSchedulerTest {
    private lateinit var app: Scheduler
    private lateinit var cmd: CommandLine
    private lateinit var sw: StringWriter

    @BeforeEach
    fun setUp() {
        app = Scheduler(InMemoryRepository(), InMemorySchedulerRepository())
        cmd = CommandLine(app)

        sw = StringWriter()

        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", sw.toString())
    }

    @Test
    fun `should show help message for schedule command`() {
        cmd.execute("--help")
        assertEquals("""
            Usage: scheduler [-hlV] [-d=<targetDate>] [-p=<postId>]
              -d=<targetDate>    Target date
              -h, --help         Show this help message and exit.
              -l                 List scheduled posts
              -p=<postId>        Post id
              -V, --version      Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should show message if post id does not exists`() {
        cmd.execute("-p", "1", "-d", "2022-10-02 at 09:00 PM")
        assertEquals("Couldn't find post with id 1", sw.toString())
    }

    @Test
    fun `should schedule post to be published`() {
        val postsRepository = InMemoryRepository()
        postsRepository.save(arrayListOf(
            SocialPosts(1, "anything")
        ))
        val app = Scheduler(postsRepository, InMemorySchedulerRepository())
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post has been scheduled", sw.toString())
    }

    @Test
    fun `should list post with id 1 to be scheduled`() {
        val postsRepository = InMemoryRepository()
        val post = SocialPosts(1, "anything")
        postsRepository.save(arrayListOf(
            post
        ))

        val scheduleRepository = InMemorySchedulerRepository()
        scheduleRepository.save(
            ScheduledItem(
            post, Instant.parse("2022-10-02T09:00:00Z")
        )
        )

        val app = Scheduler(postsRepository, scheduleRepository)
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("-l")

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
        """.trimIndent(), sw.toString() )
    }
}