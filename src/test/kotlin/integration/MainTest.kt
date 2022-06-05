package integration

import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.PrintWriter
import java.io.StringWriter

class MainTest {

    @Test
    fun `should list available commands`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.err = PrintWriter(sw)

        cmd.execute()
        assertEquals("""
            Missing required subcommand
            Usage: social [COMMAND]
            post to any social media
            Commands:
              post
              scheduler
        
        """.trimIndent(), sw.toString())
    }

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
}