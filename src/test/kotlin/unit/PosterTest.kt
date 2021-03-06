package unit

import MockedOutput
import adapters.outbound.inmemory.InMemorySchedulerRepository
import adapters.inbound.cli.Poster
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.SchedulerRepository
import application.socialnetwork.SocialThirdParty
import buildCommandLine
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class PosterTest {

    private lateinit var app: Poster
    private lateinit var cmd: CommandLine
    private val schedulerRepository: SchedulerRepository = InMemorySchedulerRepository()
    private val socialThirdParty: SocialThirdParty = mockk()
    private var currentDate: Instant = Instant.now()

    @BeforeEach
    fun setUp() {
        app = Poster(schedulerRepository, MockedOutput(), currentDate, socialThirdParty)
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
    fun `should display friendly message if there is no arguments given`() {
        cmd.execute()

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("Missing required fields", result)
    }

    @Test
    fun `should display friendly message if there is no posts to send to twitter`() {
        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("There are no posts to be posted", result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "2"])
    fun `set post to be sent to twitter`(id: String) {
        val scheduledItem = ScheduledItem(
            SocialPosts(id, "random post text"),
            Instant.parse("2014-12-22T10:15:30Z")
        )

        schedulerRepository.save(
            scheduledItem
        )

        cmd.execute("-p", id)

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("Post $id set to twitter", result)
    }

    @Test
    fun `should send post to twitter`() {
        val instantExpected = "2014-12-22T10:15:31Z"
        val clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))
        val scheduledItem = ScheduledItem(
            SocialPosts("1", "random post text"),
            Instant.parse("2014-12-22T10:15:30Z")
        )

        currentDate = Instant.now(clock)
        schedulerRepository.save(
            scheduledItem
        )

        every { socialThirdParty.send(any()) } returns scheduledItem.post

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("Post 1 sent to twitter", result)
    }

    @Test
    fun `should send multiple posts to twitter`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        val scheduledItem = ScheduledItem(
            SocialPosts(id = "1", text = "random post text"),
            Instant.parse("2014-12-22T10:15:30Z"),
            "1",
        )

        val scheduledItem2 = ScheduledItem(
            SocialPosts(id = "2", text = "another post"),
            Instant.parse("2014-12-22T09:15:30Z"),
            "2"
        )

        schedulerRepository.save(
            scheduledItem
        )
        schedulerRepository.save(
            scheduledItem2
        )

        every { socialThirdParty.send(scheduledItem) } returns scheduledItem.post
        every { socialThirdParty.send(scheduledItem2) } returns scheduledItem2.post

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("""
            Post 1 sent to twitter
            Post 2 sent to twitter
        """.trimIndent(), result)
    }

    @Test
    fun `should not post when publish date has not arrived yet`() {
        val instantExpected = "2014-12-22T10:15:31Z"
        val clock: Clock = Clock.fixed(Instant.parse(instantExpected), ZoneId.of("UTC"))

        currentDate = Instant.now(clock)
            schedulerRepository.save(
                ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )

        app = Poster(schedulerRepository, MockedOutput(), currentDate, socialThirdParty)
        cmd = CommandLine(app)

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals(
            "Waiting for the date to come to publish post 1 (scheduled for 22 Dec 2014 10:15:32)",
            result
        )
    }

    @Test
    fun `should not post posts when publish date has not arrived yet`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("2", "random post text"),
                Instant.parse("2014-12-22T10:15:32Z")
            )
        )

        app = Poster(schedulerRepository, MockedOutput(), currentDate, socialThirdParty)
        cmd = CommandLine(app)

        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("""
            Waiting for the date to come to publish post 1 (scheduled for 22 Dec 2014 10:15:32)
            Waiting for the date to come to publish post 2 (scheduled for 22 Dec 2014 10:15:32)
        """.trimIndent(), result)
    }

    @Test
    fun `should not post posts twice`() {
        currentDate = Instant.parse("2014-12-22T10:15:31Z")

        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("1", "random post text"),
                Instant.parse("2014-12-21T10:15:32Z")
            )
        )
        schedulerRepository.save(
            ScheduledItem(
                SocialPosts("2", "random post text 2"),
                Instant.parse("2014-12-21T10:15:32Z")
            )
        )
        every { socialThirdParty.send(any()) } returns mockk()

        app = Poster(schedulerRepository, MockedOutput(), currentDate, socialThirdParty)
        cmd = CommandLine(app)

        cmd.execute("-r")
        cmd.execute("-r")

        val result = cmd.getExecutionResult<String>()

        Assertions.assertEquals("There are no posts to be posted", result)
    }
}