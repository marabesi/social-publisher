package adapters.cli

import MockedOutput
import adapters.inbound.cli.Post
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialMedia
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import picocli.CommandLine
import java.time.Instant
import java.util.stream.Stream

@Suppress("ktlint:standard:max-line-length", "ktlint:standard:string-template-indent")
class ListPostTest {
    private val postsRepository = InMemoryPostRepository()
    private val schedulerRepository = InMemorySchedulerRepository()
    private val cmd = CommandLine(Post(postsRepository, schedulerRepository, MockedOutput()))

    @MethodSource("postProvider")
    @ParameterizedTest
    fun `should list post created`(text: String) {
        cmd.execute("-c", text)
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("1. $text (${text.length}/280)", result)
    }

    companion object {
        @JvmStatic
        fun postProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("a"),
                Arguments.of("b"),
            )
    }

    @Test
    fun `should list posts created`() {
        val first = "this is my first post"
        val second = "this is my second post"
        cmd.execute("-c", first)
        cmd.execute("-c", second)
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals(
            """
            1. $first (${first.length}/280)
            2. $second (${second.length}/280)
            """.trimIndent(),
            result,
        )
    }

    @Test
    fun `should list post with three dots if it is greater than 50 chars`() {
        val text =
            """
                caracters, our online editor can help you to improve word choice and writing style, and, optionally,
                help you to detect grammar mistakes and plagiarism. To check word count, simply 1
            """

        cmd.execute("-c", text)
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("1. ${text.substring(0, 50)}... (${text.length}/280)", result)
    }

    @Test
    fun `should list posts matching a fuzzy search`() {
        cmd.execute("-c", "hello desktop")
        cmd.execute("-c", "release notes")

        cmd.execute("-l", "--search", "hello desk")

        assertEquals("1. hello desktop (13/280)", cmd.getExecutionResult<String>())
    }

    @Test
    fun `should list posts by more than one id`() {
        cmd.execute("-c", "hello desktop")
        cmd.execute("-c", "release notes")
        cmd.execute("-c", "third post")

        cmd.execute("-l", "--ids", "1,3")

        assertEquals(
            """
            1. hello desktop (13/280)
            3. third post (10/280)
            """.trimIndent(),
            cmd.getExecutionResult<String>(),
        )
    }

    @Test
    fun `should list posts scheduled for a social media`() {
        cmd.execute("-c", "hello desktop")
        cmd.execute("-c", "release notes")
        schedulerRepository.save(
            ScheduledItem(postsRepository.findById("2")!!, Instant.EPOCH, socialMedia = SocialMedia.LINKEDIN),
        )

        cmd.execute("-l", "--social-media", "LINKEDIN")

        assertEquals("2. release notes (13/280)", cmd.getExecutionResult<String>())
    }
}
