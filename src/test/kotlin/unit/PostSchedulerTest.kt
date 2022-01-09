package unit

import Scheduler
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class PostSchedulerTest {

    @Test
    fun `should show help message for schedule command`() {
        val app = Scheduler()
        val cmd = CommandLine(app)

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)

        cmd.execute("--help")
        assertEquals("""
            Usage: schedule [-hV]
              -h, --help      Show this help message and exit.
              -V, --version   Print version information and exit.
        
        """.trimIndent(), sw.toString())
    }
}