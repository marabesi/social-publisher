package unit

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerCrud
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
import org.junit.jupiter.params.provider.MethodSource
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Instant
import java.util.stream.Stream

class SchedulerCrudTest {
    private lateinit var app: SchedulerCrud
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = SchedulerCrud(InMemorySchedulerRepository(), MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
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
            Usage: social scheduler [-hrV] [-id=<scheduleId>] [-p=<postId>] [COMMAND]
              -h, --help             Show this help message and exit.
                  -id=<scheduleId>   Scheduled id
              -p=<postId>            Post id
              -r                     Sets the cli to remove a scheduled post
              -V, --version          Print version information and exit.
            Commands:
              list
              create

        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should not allow remove parameter without post id`() {
        val scheduleRepository = InMemorySchedulerRepository()

        val app = SchedulerCrud(scheduleRepository, MockedOutput())
        val cmd = CommandLine(app)
        cmd.execute("-r")

        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @ParameterizedTest
    @MethodSource("scheduleProvider")
    fun `should remove schedule from posts by schedule id`(scheduleId: String, postId: String) {
        val post1 = SocialPosts(id = postId, text = "anything")

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

        val app = SchedulerCrud(scheduleRepository, MockedOutput())
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
