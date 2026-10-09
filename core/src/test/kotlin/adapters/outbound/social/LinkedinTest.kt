package adapters.outbound.social

import application.entities.LinkedInCredentials
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.MissingConfigurationSetup
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinkedinTest {
    private val scheduledPost =
        ScheduledItem(
            SocialPosts("1", "Random linkedin post"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    private val linkedin: Linkedin = mockk()
    private val configurationRepository: ConfigurationRepository = mockk()

    private fun buildLinkedin(configuration: SocialConfiguration): LinkedinCredentialsValidator {
        every { configurationRepository.find() } returns configuration

        return LinkedinCredentialsValidator(configurationRepository, linkedin)
    }

    @Test
    fun `should handle missing linkedin credentials`() {
        val validator = buildLinkedin(SocialConfiguration())

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: linkedin", exception.message)
    }

    @Test
    fun `should handle missing access token`() {
        val validator = buildLinkedin(SocialConfiguration(linkedin = LinkedInCredentials()))

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: access token", exception.message)
    }

    @Test
    fun `should handle missing author urn`() {
        val validator =
            buildLinkedin(
                SocialConfiguration(linkedin = LinkedInCredentials(accessToken = "token")),
            )

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: author urn", exception.message)
    }

    @Test
    fun `should send a post to linkedin`() {
        val published =
            SocialPosts(
                scheduledPost.post.id,
                scheduledPost.post.text,
                "urn:li:share:1",
            )
        every { linkedin.publish(any()) } returns published

        val validator =
            buildLinkedin(
                SocialConfiguration(linkedin = LinkedInCredentials(accessToken = "token", authorUrn = "urn:li:person:1")),
            )

        validator.send(scheduledPost)

        verify(exactly = 1) { linkedin.publish(scheduledPost.post.text) }
    }
}
