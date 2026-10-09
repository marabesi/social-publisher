package adapters.cli

import MockedOutput
import adapters.inbound.cli.linkedin.LinkedinToken
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import application.socialnetwork.ExchangeLinkedInAuthorization
import application.socialnetwork.LinkedInToken
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import picocli.CommandLine

class LinkedinTokenTest {
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val exchange: ExchangeLinkedInAuthorization = mockk()

    @Test
    fun `stores the exchanged token`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", clientSecret = "secret", redirectUri = "https://x/cb"),
            ),
        )
        every { exchange.exchange("code") } returns LinkedInToken("token", "urn:li:person:1")

        val cmd = CommandLine(LinkedinToken(configurationRepository, exchange, MockedOutput()))
        cmd.execute("--code", "code")

        assertEquals("LinkedIn account connected", cmd.getExecutionResult<String>())
        assertEquals("token", configurationRepository.find().linkedin?.accessToken)
    }

    @Test
    fun `requires the code option`() {
        val cmd = CommandLine(LinkedinToken(configurationRepository, exchange, MockedOutput()))

        assertEquals(2, cmd.execute())
    }
}
