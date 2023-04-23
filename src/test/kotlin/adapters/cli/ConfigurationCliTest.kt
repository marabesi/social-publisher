package adapters.cli

import MockedOutput
import adapters.inbound.cli.Configuration
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.Messages
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import picocli.CommandLine

@Suppress("MaxLineLength")
class ConfigurationCliTest {
    private lateinit var app: Configuration
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = Configuration(MockedOutput(), ConfigurationInMemoryRepository())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        val code = cmd.execute()

        assertEquals(0, code)
        assertEquals(Messages.MISSING_REQUIRED_FIELDS, cmd.getExecutionResult())
    }

    @Test
    fun `should store configuration`() {
        val configuration = """{}"""

        val code = cmd.execute("-c", configuration)

        assertEquals(0, code)
        assertEquals("Configuration has been stored", cmd.getExecutionResult())
    }

    @Test
    fun `should show stored configuration`() {
        val configuration = """{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"}}"""
        val expectedConfiguration = """{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"},"timezone":"UTC"}"""

        cmd.execute("-c", configuration)
        val code = cmd.execute("-l")

        assertEquals(0, code)
        assertEquals(expectedConfiguration, cmd.getExecutionResult())
    }
}
