package thirdpartyintegration

import adapters.outbound.social.LinkedInOAuthClient
import adapters.outbound.social.Linkedin
import adapters.outbound.social.LinkedinCredentialsValidator
import application.entities.LinkedInCredentials
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LinkedinClient {
    private val configurationRepository: ConfigurationRepository = mockk()

    private val scheduledPost =
        ScheduledItem(
            SocialPosts("1", "Random linkedin post"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    @BeforeEach
    fun setUp() {
        WireMockLinkedIn.start()
    }

    @AfterEach
    fun tearDown() {
        WireMockLinkedIn.stop()
    }

    @Test
    fun `should send a post through the linkedin api`() {
        every { configurationRepository.find() } returns
            SocialConfiguration(
                linkedin = LinkedInCredentials(accessToken = "test-access-token", authorUrn = "urn:li:person:123"),
            )

        val linkedin =
            LinkedinCredentialsValidator(
                configurationRepository,
                Linkedin(configurationRepository),
            )

        val post = linkedin.send(scheduledPost)

        assertNotNull(post.socialMediaId)
        assertEquals("urn:li:share:1", post.socialMediaId)
    }

    @Test
    fun `should exchange the authorization code for an access token and author urn`() {
        every { configurationRepository.find() } returns
            SocialConfiguration(
                linkedin =
                    LinkedInCredentials(
                        clientId = "client-id",
                        clientSecret = "client-secret",
                        redirectUri = "https://example.com/callback",
                    ),
            )

        val token = LinkedInOAuthClient(configurationRepository).exchange("the-code")

        assertEquals("test-linkedin-access-token", token.accessToken)
        assertEquals("urn:li:person:test-member-id", token.authorUrn)
    }
}
