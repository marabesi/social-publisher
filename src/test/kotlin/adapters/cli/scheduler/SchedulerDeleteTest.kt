package adapters.cli.scheduler

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerDelete
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import application.entities.ScheduledItem
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import picocli.CommandLine
import java.time.Instant
import java.util.stream.Stream

class SchedulerDeleteTest {
    private lateinit var app: SchedulerDelete
    private lateinit var cmd: CommandLine
    private val scheduleRepository = InMemorySchedulerRepository()

    @BeforeEach
    fun setUp() {
        app = SchedulerDelete(scheduleRepository, MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should not allow remove parameter without post id`() {
        cmd.execute()

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, cmd.getExecutionResult())
    }

    @ParameterizedTest
    @MethodSource("scheduleProvider")
    fun `should remove schedule from posts by schedule id`(scheduleId: String, postId: String) {
        val post1 = SocialPosts(id = postId, text = "anything")

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

        cmd.execute("-id", scheduleId)

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
