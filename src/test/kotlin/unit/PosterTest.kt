package unit

import buildCommandLine
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.PrintWriter
import java.io.StringWriter

class PosterTest {

    @Test
    fun `should show help message for poster command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("poster", "--help")
        Assertions.assertEquals(
            """
            Usage: social poster [-hV] [-p=<postId>] [-s=<socialMedia>]
              -h, --help          Show this help message and exit.
              -p=<postId>         Post Id
              -s=<socialMedia>    Social media to post
              -V, --version       Print version information and exit.
        
        """.trimIndent(), sw.toString()
        )
    }
}