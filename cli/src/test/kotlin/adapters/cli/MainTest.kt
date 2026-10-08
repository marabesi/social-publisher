package adapters.cli

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
        assertEquals(
            """
            Missing required subcommand
            Usage: social [-hV] [COMMAND]
            post to any social media
              -h, --help      Show this help message and exit.
              -V, --version   Print version information and exit.
            Commands:
              post
              scheduler
              poster
              configuration
              desktop
              rest
            
            """.trimIndent(),
            sw.toString(),
        )
    }
}
