package unit

import SchedulerRepository
import buildCommandLine
import cli.Poster
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import persistence.InMemorySchedulerRepository
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter

class PosterTest {

    private lateinit var app: Poster
    private lateinit var cmd: CommandLine
    private val schedulerRepository: SchedulerRepository = InMemorySchedulerRepository()

    @BeforeEach
    fun setUp() {
        app = Poster(schedulerRepository, MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show help message for poster command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("poster", "--help")
        Assertions.assertEquals(
            """
            Usage: social poster [-hrV] [-p=<postId>] [-s=<socialMedia>]
              -h, --help          Show this help message and exit.
              -p=<postId>         Post Id
              -r                  Executes routine that goes over and post posts
              -s=<socialMedia>    Social media to post
              -V, --version       Print version information and exit.
        
        """.trimIndent(), sw.toString()
        )
    }

    @Test
    fun `should display friendly message if there is no posts to send to twitter`() {
        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("There are no posts to be posted", result)
    }
}