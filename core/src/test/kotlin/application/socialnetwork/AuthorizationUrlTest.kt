package application.socialnetwork

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthorizationUrlTest {
    private val configurationRepository = ConfigurationInMemoryRepository()

    @Test
    fun `builds the url from the stored credentials`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", redirectUri = "https://example.com/callback"),
            ),
        )

        val result = AuthorizationUrl(configurationRepository, MockedOutput()).invoke("state")

        assertTrue(result.contains("client_id=id"))
        assertTrue(result.contains("state=state"))
    }

    @Test
    fun `asks for the client id when missing`() {
        configurationRepository.save(SocialConfiguration(linkedin = LinkedInCredentials()))

        val result = AuthorizationUrl(configurationRepository, MockedOutput()).invoke()

        assertEquals("Missing required configuration: linkedin client id", result)
    }

    @Test
    fun `asks for the configuration when none is stored`() {
        val result = AuthorizationUrl(ConfigurationInMemoryRepository(), MockedOutput()).invoke()

        assertEquals("Missing required configuration: linkedin", result)
    }
}
