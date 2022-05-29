package unit

import Scheduler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import socialPosts.SocialPosts
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class PostSchedulerTest {
    private lateinit var app: Scheduler
    private lateinit var cmd: CommandLine
    private lateinit var sw: StringWriter

    @BeforeEach
    fun setUp() {
        app = Scheduler(InMemoryRepository())
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
            Usage: scheduler [-hV] [-d=<targetDate>] [-p=<postId>]
              -d=<targetDate>    Target date
              -h, --help         Show this help message and exit.
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
        val app = Scheduler(postsRepository)
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("-p", "1", "-d", "2022-10-02 at 09:00 PM")
        assertEquals("Post has been scheduled", sw.toString())
    }
}