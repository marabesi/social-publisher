package application.configuration

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConfigurationListTest {
    private lateinit var listConfiguration: List
    private lateinit var configurationInMemoryRepository: ConfigurationInMemoryRepository

    @BeforeEach
    fun setUp() {
        configurationInMemoryRepository = ConfigurationInMemoryRepository()
        listConfiguration = List(MockedOutput(), configurationInMemoryRepository)
    }

    @Test
    fun `should handle fetch configuration when it does not exists`() {
        val result = listConfiguration.invoke()
        Assertions.assertEquals("There is no configuration stored", result)
    }

    @Test
    fun `should list back whatever the configuration was set with`() {
        configurationInMemoryRepository.save(
            SocialConfiguration("my file")
        )

        val result = listConfiguration.invoke()
        Assertions.assertEquals("""{"fileName":"my file"}""", result)
    }
}