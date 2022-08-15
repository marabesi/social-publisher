package unit

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerCreate
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import picocli.CommandLine

class SchedulerCreateTest {
    private lateinit var app: SchedulerCreate
    private lateinit var cmd: CommandLine
    private val postsRepository = InMemoryPostRepository()

    @BeforeEach
    fun setUp() {
        app = SchedulerCreate(postsRepository, InMemorySchedulerRepository(), MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @ParameterizedTest
    @ValueSource(strings = ["1", "2"])
    fun `should show message if post id does not exists`(id: String) {
        cmd.execute("-p", id, "-d", "2022-10-02 at 09:00 PM")
        assertEquals("Couldn't find post with id $id", cmd.getExecutionResult())
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
    fun `should schedule post to be published`() {
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))

        cmd.execute("-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post has been scheduled", cmd.getExecutionResult())
    }

    @Test
    fun `should not schedule post twice on the same date time to be published`() {
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))

        cmd.execute("-p", "1", "-d", "2022-10-02T09:00:00Z")
        cmd.execute("-p", "1", "-d", "2022-10-02T09:00:00Z")
        assertEquals("Post is already scheduled for 2022-10-02T09:00:00Z", cmd.getExecutionResult())
    }

    @Test
    fun `should warn when invalid date is given to be published`() {
        postsRepository.save(arrayListOf(
            SocialPosts(text = "anything")
        ))

        cmd.execute("-p", "1", "-d", "2022")
        assertEquals("Invalid date time to schedule post", cmd.getExecutionResult())
    }
}
