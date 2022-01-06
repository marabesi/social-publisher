package acceptance

import Post
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should show help message for post command`() {
        val app = Post()
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("--help")
        assertEquals("""
            Usage: post [-hV]
              -h, --help      Show this help message and exit.
              -V, --version   Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }
}