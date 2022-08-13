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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.util.stream.Stream

class SchedulerTest {
    private lateinit var app: Scheduler
    private lateinit var cmd: CommandLine
    private val currentTime = Instant.parse("2022-08-13T11:11:00Z")

    @BeforeEach
    fun setUp() {
        app = Scheduler(InMemoryPostRepository(), InMemorySchedulerRepository(), MockedOutput(), currentTime)
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
            Usage: social scheduler [-chrV] [-d=<targetDate>] [-id=<scheduleId>]
                                    [-p=<postId>] [-s=<socialMedia>] [COMMAND]
              -c                     Sets the cli to schedule a post
              -d=<targetDate>        Target date
              -h, --help             Show this help message and exit.
                  -id=<scheduleId>   Scheduled id
              -p=<postId>            Post id
              -r                     Sets the cli to remove a scheduled post
              -s=<socialMedia>       Social media
              -V, --version          Print version information and exit.
            Commands:
              list

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
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput(), currentTime)
        val cmd = CommandLine(app)

        cmd.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post has been scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should not schedule post twice on the same date time to be published`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput(), currentTime)
        val cmd = CommandLine(app)

        cmd.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        cmd.execute("-c", "-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post is already scheduled for 2022-10-02T09:00:00Z", cmd.getExecutionResult())
    }

    @Test
    fun `should warn when invalid date is given to be published`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput(), currentTime)
        val cmd = CommandLine(app)

        cmd.execute("-c", "-p", "1", "-d", "2022")
        assertEquals("Invalid date time to schedule post", cmd.getExecutionResult())
    }

    @Test
    fun `should not allow remove parameter without post id`() {
        val postsRepository = InMemoryPostRepository()
        val scheduleRepository = InMemorySchedulerRepository()

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput(), currentTime)
        val cmd = CommandLine(app)
        cmd.execute("-r")

        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @ParameterizedTest
    @MethodSource("scheduleProvider")
    fun `should remove schedule from posts by schedule id`(scheduleId: String, postId: String) {
        val postsRepository = InMemoryPostRepository()
        val post1 = SocialPosts(id = postId, text = "anything")
        postsRepository.save(arrayListOf(
            post1,
        ))

        val scheduleRepository = InMemorySchedulerRepository()
        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2022-10-02T09:00:00Z")
            ),
        )
        scheduleRepository.save(
            ScheduledItem(
                post1, Instant.parse("2022-10-02T09:00:00Z")
            ),
        )

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput(), currentTime)
        val cmd = CommandLine(app)
        cmd.execute("-r", "-id", scheduleId)

        assertEquals("Schedule $scheduleId has been removed from post $postId", cmd.getExecutionResult())
    }

    companion object {
        @JvmStatic
        fun scheduleProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("1", "1"),
                Arguments.of("2", "1"),
            )
        }
    }
}