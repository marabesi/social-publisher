package adapters.cli

import MockedOutput
import buildCommandLine
import adapters.inbound.cli.Post
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import adapters.outbound.inmemory.InMemoryPostRepository
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter

class PostCreatorTest {
    private val cmd = CommandLine(Post(InMemoryPostRepository(), MockedOutput()))

    @Test
    fun `should show help message for post command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("post", "--help")
        assertEquals("""
            Usage: social post [-hlV] [-c=<text>]
              -c=<text>       Creates a post
              -h, --help      Show this help message and exit.
              -l              List created posts
              -V, --version   Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should show friendly message when no arguments is provided to post`() {
        cmd.execute()

        val result = cmd.getExecutionResult<String>()

        assertEquals("Missing required fields", result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["this is my first post", "<b>another</b>"])
    fun `should create post with the desired text`(text: String) {
        cmd.execute("-c", text)
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            Post has been created
        """.trimIndent(), result)
    }

    @Test
    fun `should give a message when no posts exists`() {
        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals("""
            No post found
        """.trimIndent(), result)
    }
}
