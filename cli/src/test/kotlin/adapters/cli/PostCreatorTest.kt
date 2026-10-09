package adapters.cli

import MockedOutput
import adapters.inbound.cli.Post
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.Messages
import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter

class PostCreatorTest {
    private val cmd = CommandLine(Post(InMemoryPostRepository(), InMemorySchedulerRepository(), MockedOutput()))

    @Test
    fun `should show help message for post command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("post", "--help")
        assertEquals(
            listOf(
                "Usage: social post [-hlV] [-c=<text>] [--ids=<ids>] [--search=<search>]",
                "                   [--social-media=<socialMedia>]",
                "  -c=<text>               Creates a post",
                "  -h, --help              Show this help message and exit.",
                "      --ids=<ids>         List posts whose ids match the comma separated list",
                "  -l                      List created posts",
                "      --search=<search>   List posts matching the fuzzy text search",
                "      --social-media=<socialMedia>",
                "                          List posts scheduled for the given social media",
                "  -V, --version           Print version information and exit.",
                "",
            ).joinToString("\n"),
            sw.toString(),
        )
    }

    @Test
    fun `should show friendly message when no arguments is provided to post`() {
        cmd.execute()

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, cmd.getExecutionResult())
    }

    @ParameterizedTest
    @ValueSource(strings = ["this is my first post", "<b>another</b>"])
    fun `should create post with the desired text`(text: String) {
        cmd.execute("-c", text)
        val result = cmd.getExecutionResult<String>()

        assertEquals(
            """
            Post has been created
            """.trimIndent(),
            result,
        )
    }

    @Test
    fun `should give a message when no posts exists`() {
        cmd.execute("-l")
        val result = cmd.getExecutionResult<String>()

        assertEquals(
            """
            No post found
            """.trimIndent(),
            result,
        )
    }
}
