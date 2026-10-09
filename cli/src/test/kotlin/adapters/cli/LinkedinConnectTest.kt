package adapters.cli

import MockedOutput
import adapters.inbound.cli.linkedin.LinkedinConnect
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import picocli.CommandLine

class LinkedinConnectTest {
    private val configurationRepository = ConfigurationInMemoryRepository()
    private val cmd = CommandLine(LinkedinConnect(configurationRepository, MockedOutput()))

    @Test
    fun `prints the authorization url`() {
        configurationRepository.save(
            SocialConfiguration(
                linkedin = LinkedInCredentials(clientId = "id", redirectUri = "https://example.com/callback"),
            ),
        )

        cmd.execute("--state", "state-1")
        val result = cmd.getExecutionResult<String>()

        assertTrue(result.startsWith("https://www.linkedin.com/oauth/v2/authorization?"))
        assertTrue(result.contains("state=state-1"))
    }

    @Test
    fun `asks for the client id when missing`() {
        configurationRepository.save(SocialConfiguration(linkedin = LinkedInCredentials()))

        cmd.execute()

        assertEquals("Missing required configuration: linkedin client id", cmd.getExecutionResult<String>())
    }
}
