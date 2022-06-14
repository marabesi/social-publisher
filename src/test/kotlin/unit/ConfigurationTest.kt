package unit

import cli.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import persistence.ConfigurationInMemoryRepository
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
    fun `should store json content as a configuration`() {
        cmd.execute("-c", "{}", "-p", "tmp")
        assertEquals("Configuration has been stored", cmd.getExecutionResult())
    }

    @ParameterizedTest
    @MethodSource("configurationProvider")
    fun `should show stored configuration`(configuration: String, path: String, expectedConfiguration: String) {
        cmd.execute("-c", configuration, "-p", path)
        cmd.execute("-l")

        assertEquals(expectedConfiguration, cmd.getExecutionResult())
    }

    companion object {
        @JvmStatic
        fun configurationProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("{}", "tmp", """{"path":"tmp/configuration.json"}"""),
                Arguments.of("{}", "123", """{"path":"123/configuration.json"}"""),
                // ignore unknown keys
                Arguments.of("""{"banana":"banana"}""", "123", """{"path":"123/configuration.json"}"""),
            );
        }
    }
}