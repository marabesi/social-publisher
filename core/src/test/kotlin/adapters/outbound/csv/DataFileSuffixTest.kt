package adapters.outbound.csv

import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DataFileSuffixTest {
    @Test
    fun `falls back to production without a configuration`() {
        assertEquals("production", DataFileSuffix(configurationRepository = null).value())
        assertEquals("production", DataFileSuffix(ConfigurationInMemoryRepository()).value())
    }

    @Test
    fun `uses the configuration file name as the suffix`() {
        val configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(fileName = "e2e-file"))

        assertEquals("e2e-file", DataFileSuffix(configurationRepository).value())
    }

    @Test
    fun `falls back to production when the file name is blank`() {
        val configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(fileName = "  "))

        assertEquals("production", DataFileSuffix(configurationRepository).value())
    }
}
