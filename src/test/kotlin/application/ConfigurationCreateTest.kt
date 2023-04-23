package application

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.configuration.Create
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConfigurationCreateTest {

    private lateinit var createConfiguration: Create

    @BeforeEach
    fun setUp() {
        createConfiguration = Create(MockedOutput(), ConfigurationInMemoryRepository())
    }

    @ParameterizedTest
    @MethodSource("storeConfigurationSuccessfully")
    fun `should store json content as a configuration`(configuration: String) {
        val result = createConfiguration.invoke(configuration)
        Assertions.assertEquals("Configuration has been stored", result)
    }

    @ParameterizedTest
    @MethodSource("invalidConfigurationProvider")
    fun `should inform invalid key when trying to store json content as a configuration`(
        configuration: String,
        expectedOutput: String
    ) {
        val result = createConfiguration.invoke(configuration)
        Assertions.assertEquals(expectedOutput, result)
    }

    companion object {
        @JvmStatic
        fun storeConfigurationSuccessfully(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("{}"),
                Arguments.of("""{"fileName":"aaa","timezone":""}"""),
            )
        }

        @JvmStatic
        fun invalidConfigurationProvider(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("""{"random":"aaa"}""", """The give key random is not supported"""),
                Arguments.of("""{"another":"abc"}""", """The give key another is not supported"""),
            )
        }
    }
}