package unit

import application.Post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import adapters.outbound.inmemory.InMemoryRepository
import picocli.CommandLine
import java.util.stream.Stream

class ListPostTest {
    private val cmd = CommandLine(Post(InMemoryRepository(), MockedOutput()))

    @MethodSource("postProvider")
    @ParameterizedTest
    fun `should list post created`(text: String, expected: String) {
        cmd.execute("-c", text)
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals(expected.trimIndent(), result)
    }

    companion object {
        @JvmStatic
        fun postProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("a", "1. a"),
                Arguments.of("b", "1. b"),
            );
        }
    }

    @Test
    fun `should list posts created`() {
        cmd.execute("-c", "this is my first post")
        cmd.execute("-c", "this is my second post")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. this is my first post
            2. this is my second post
        """.trimIndent(), result)
    }

    @Test
    fun `should list post with three dots if it is greater than 50 chars`() {
        cmd.execute("-c", "caracters, our online editor can help you to improve word choice and writing style, and, optionally, help you to detect grammar mistakes and plagiarism. To check word count, simply 1")
        cmd.execute("-l")

        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            1. caracters, our online editor can help you to impro...
        """.trimIndent(), result)
    }
}
