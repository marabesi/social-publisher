package unit

import MockedOutput
import adapters.inbound.cli.scheduler.SchedulerCrud
import buildCommandLine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter

class SchedulerCrudTest {
    private lateinit var app: SchedulerCrud
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = SchedulerCrud(MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @Test
    fun `should show help message for schedule command`() {
        val cmd = buildCommandLine()

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute("scheduler", "--help")
        assertEquals("""
            Usage: social scheduler [-hV] [COMMAND]
              -h, --help      Show this help message and exit.
              -V, --version   Print version information and exit.
            Commands:
              list
              create
              delete

        """.trimIndent(), sw.toString())
    }
}
