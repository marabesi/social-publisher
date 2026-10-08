package application.configuration

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConfigurationCreateTest {
    private lateinit var createConfiguration: Create
    private lateinit var configurationRepository: ConfigurationInMemoryRepository

    @BeforeEach
    fun setUp() {
        configurationRepository = ConfigurationInMemoryRepository()
        createConfiguration = Create(MockedOutput(), configurationRepository)
    }

    @Test
    fun `should apply defaults when storing a structured configuration`() {
        val result = createConfiguration.invoke(SocialConfiguration(fileName = "prod"))

        Assertions.assertEquals("Configuration has been stored", result)
        Assertions.assertEquals("prod", configurationRepository.find().fileName)
        Assertions.assertEquals("csv", configurationRepository.find().storage)
        Assertions.assertEquals("UTC", configurationRepository.find().timezone)
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
        expectedOutput: String,
    ) {
        val result = createConfiguration.invoke(configuration)
        Assertions.assertEquals(expectedOutput, result)
    }

    companion object {
        @JvmStatic
        fun storeConfigurationSuccessfully(): Stream<Arguments> =
            Stream.of(
                Arguments.of("{}"),
                Arguments.of("""{"fileName":"aaa","timezone":""}"""),
            )

        @JvmStatic
        fun invalidConfigurationProvider(): Stream<Arguments> =
            Stream.of(
                Arguments.of("""{"random":"aaa"}""", """The give key random is not supported"""),
                Arguments.of("""{"another":"abc"}""", """The give key another is not supported"""),
            )
    }
}
