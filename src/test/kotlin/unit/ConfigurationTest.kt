package unit

import MockedOutput
import adapters.inbound.cli.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import picocli.CommandLine
import java.util.stream.Stream

class ConfigurationTest {
    private lateinit var app: Configuration
    private lateinit var cmd: CommandLine

    @BeforeEach
    fun setUp() {
        app = Configuration(MockedOutput(), ConfigurationInMemoryRepository())
        cmd = CommandLine(app)
    }

    @Test
    fun `should show friendly message when required fields are not provided`() {
        cmd.execute()
        assertEquals("Missing required fields", cmd.getExecutionResult())
    }

    @Test
    fun `should handle fetch configuration when it does not exists`() {
        cmd.execute("-l")
        assertEquals("There is no configuration stored", cmd.getExecutionResult())
    }

    @Test
    fun `should store json content as a configuration`() {
        cmd.execute("-c", "{}")
        assertEquals("Configuration has been stored", cmd.getExecutionResult())
    }

    @ParameterizedTest
    @MethodSource("configurationProvider")
    fun `should show stored configuration`(configuration: String, expectedConfiguration: String) {
        cmd.execute("-c", configuration)
        cmd.execute("-l")

        assertEquals(expectedConfiguration, cmd.getExecutionResult())
    }

    companion object {
        @JvmStatic
        fun configurationProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("""{"fileName":"tmp"}""", """{"fileName":"tmp","storage":"csv"}"""),
                Arguments.of("""{"fileName":"123"}""", """{"fileName":"123","storage":"csv"}"""),
//                Arguments.of("""{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"","consumerSecret":"","accessToken":"","accessTokenSecret":""}}""", """{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"","consumerSecret":"","accessToken":"","accessTokenSecret":""}}"""),
                Arguments.of("""{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"}}""", """{"fileName":"e2e-file","storage":"csv","twitter":{"consumerKey":"1","consumerSecret":"1","accessToken":"1","accessTokenSecret":"1"}}""")
            );
        }
    }
}