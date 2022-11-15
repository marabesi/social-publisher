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
        assertEquals(
            """
            Missing required subcommand
            Usage: social [COMMAND]
            post to any social media
            Commands:
              post
              scheduler
              poster
              configuration
        
            """.trimIndent(),
            sw.toString()
        )
    }
}
