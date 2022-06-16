package unit

import application.persistence.SchedulerRepository
import buildCommandLine
import application.Poster
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import adapters.outbound.inmemory.InMemorySchedulerRepository
import picocli.CommandLine
import application.entities.ScheduledItem
import application.entities.SocialPosts
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class PosterTest {

    private lateinit var app: Poster
    private lateinit var cmd: CommandLine
    private val schedulerRepository: SchedulerRepository = InMemorySchedulerRepository()
    private var currentDate: Instant = Instant.now()

    @BeforeEach
    fun setUp() {
        app = Poster(schedulerRepository, MockedOutput(), currentDate)
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

    @Test
    fun `should send post with id 1 to twitter`() {
        val instantExpected = "2014-12-22T10:15:31Z"
        val clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))

        currentDate = Instant.now(clock)
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts(1, "random post text"),
                Instant.parse("2014-12-22T10:15:30Z")
            )
        )

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("Post 1 sent to twitter", result)
    }

    @Test
    fun `should not post posts when publish date has not arrived yet`() {
        val instantExpected = "2014-12-22T10:15:31Z"
        val clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))

        currentDate = Instant.now(clock)
            schedulerRepository.save(
                ScheduledItem(
                SocialPosts(1, "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )

        app = Poster(schedulerRepository, MockedOutput(), currentDate)
        cmd = CommandLine(app)

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("Waiting for the date to come to publish post 1", result)
    }
}