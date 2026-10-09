package adapters.inbound.rest

import MockedOutput
import adapters.inbound.rest.dto.LinkedInTokenRequest
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import application.socialnetwork.ExchangeLinkedInAuthorization
import application.socialnetwork.LinkedInToken
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LinkedinControllerTest {
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val exchange: ExchangeLinkedInAuthorization = mockk()
    private val controller = LinkedinController(configurationRepository, exchange, MockedOutput())

    @Test
    fun `returns the authorization url`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", redirectUri = "https://example.com/callback"),
            ),
        )

        val response = controller.authorizationUrl("state")

        assertTrue(response.message.startsWith("https://www.linkedin.com/oauth/v2/authorization?"))
        assertTrue(response.message.contains("state=state"))
    }

    @Test
    fun `exchanges the code and stores the token`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", clientSecret = "secret", redirectUri = "https://x/cb"),
            ),
        )
        every { exchange.exchange("code") } returns LinkedInToken("token", "urn:li:person:1")

        val response = controller.token(LinkedInTokenRequest("code"))

        assertEquals("LinkedIn account connected", response.message)
        assertEquals("token", configurationRepository.find().linkedin?.accessToken)
    }
}
