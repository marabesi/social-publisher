package acceptance

import Application
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class MainTest {

    @Test
    fun `should list available commands`() {
        val cmd = CommandLine(Application())

        val sw = StringWriter()
        cmd.err = PrintWriter(sw)

        cmd.execute()
        assertEquals("""
            Missing required subcommand
            Usage: social [COMMAND]
            post to any social media
            Commands:
              post
              schedule
        
        """.trimIndent(), sw.toString())
    }

    @Test
    fun `should show help message for post command`() {
        val app = Application()
        val cmd = CommandLine(app)

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