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
            Usage: social scheduler [-chlrV] [-d=<targetDate>] [-p=<postId>]
                                    [-s=<scheduleId>]
              -c                 Sets the cli to schedule a post
              -d=<targetDate>    Target date
              -h, --help         Show this help message and exit.
              -l                 List scheduled posts
              -p=<postId>        Post id
              -r                 Sets the cli to remove a scheduled post
              -s=<scheduleId>    Scheduled id
              -V, --version      Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should show message if scheduler is empty`() {
        cmd.execute("-l")
        assertEquals("No posts scheduled", cmd.getExecutionResult())
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
    fun `should not schedule post twice on the same date time to be published`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput())
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
        val app = Scheduler(postsRepository, InMemorySchedulerRepository(), MockedOutput())
        val cmd = CommandLine(app)

        cmd.execute("-c", "-p", "1", "-d", "2022")
        assertEquals("Invalid date time to schedule post", cmd.getExecutionResult())
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
    fun `should schedule same post twice each on different days`() {
        val postsRepository = InMemoryPostRepository()
        val post = SocialPosts(text = "first day")
        postsRepository.save(arrayListOf(
            post,
        ))

        val scheduleRepository = InMemorySchedulerRepository()
        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-02T09:00:00Z")
            )
        )
        scheduleRepository.save(
            ScheduledItem(
                post, Instant.parse("2022-10-03T09:00:00Z")
            )
        )

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput())
        val cmd = CommandLine(app)
        cmd.execute("-l")

        assertEquals("""
            1. Post with id 1 will be published on 2022-10-02T09:00:00Z
            2. Post with id 1 will be published on 2022-10-03T09:00:00Z
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

    @Test
    fun `should not allow remove parameter without post id`() {
        val postsRepository = InMemoryPostRepository()
        val scheduleRepository = InMemorySchedulerRepository()

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput())
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

        val app = Scheduler(postsRepository, scheduleRepository, MockedOutput())
        val cmd = CommandLine(app)
        cmd.execute("-r", "-s", scheduleId)

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