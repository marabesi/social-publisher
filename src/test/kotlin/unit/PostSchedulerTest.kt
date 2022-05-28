package unit

import Scheduler
import org.junit.jupiter.api.Test
import persistence.InMemoryRepository
import picocli.CommandLine
import socialPosts.SocialPosts
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class PostSchedulerTest {

    @Test
    fun `should show help message for schedule command`() {
        val app = Scheduler(InMemoryRepository())
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("--help")
        assertEquals("""
            Usage: schedule [-hV] [-d=<targetDate>] [-p=<postId>]
              -d=<targetDate>    Target date
              -h, --help         Show this help message and exit.
              -p=<postId>        Post id
              -V, --version      Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should show message if post id does not exists`() {
        val postsRepository = InMemoryRepository()
        val app = Scheduler(postsRepository)
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

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