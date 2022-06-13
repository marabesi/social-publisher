package unit

import cli.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine

class ConfigurationTest {
    private lateinit var app: Configuration
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = Configuration(MockedOutput())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @Test
    fun `should store json content as a configuration`() {
        cmd.execute("-c", "{}", "-p", "tmp")
        assertEquals("Configuration has been stored", cmd.getExecutionResult())
    }
}