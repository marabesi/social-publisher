package application.socialnetwork

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.Messages
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConnectLinkedInTest {
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val exchange: ExchangeLinkedInAuthorization = mockk()

    @Test
    fun `exchanges the code and stores the token`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", clientSecret = "secret", redirectUri = "https://x/cb"),
            ),
        )
        every { exchange.exchange("the-code") } returns LinkedInToken("token-123", "urn:li:person:1")

        val result = ConnectLinkedIn(configurationRepository, exchange, MockedOutput()).invoke("the-code")

        assertEquals("LinkedIn account connected", result)
        assertEquals("token-123", configurationRepository.find().linkedin?.accessToken)
        assertEquals("urn:li:person:1", configurationRepository.find().linkedin?.authorUrn)
    }

    @Test
    fun `requires the code`() {
        val result = ConnectLinkedIn(configurationRepository, exchange, MockedOutput()).invoke(" ")

        assertEquals(Messages.MISSING_REQUIRED_FIELDS, result)
    }

    @Test
    fun `requires the client credentials`() {
        configurationRepository.save(SocialConfiguration(linkedin = LinkedInCredentials(clientId = "id")))

        val result = ConnectLinkedIn(configurationRepository, exchange, MockedOutput()).invoke("code")

        assertEquals("Missing required configuration: linkedin client secret", result)
    }

    @Test
    fun `requires a configuration`() {
        val result = ConnectLinkedIn(ConfigurationInMemoryRepository(), exchange, MockedOutput()).invoke("code")

        assertEquals("Missing required configuration: linkedin", result)
    }
}
