package adapters.outbound.social

import adapters.outbound.bluesky.Bluesky
import adapters.outbound.bluesky.BlueskyCredentialsValidator
import application.entities.BlueskyCredentials
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

class BlueskyTest {
    private val scheduledPost =
        ScheduledItem(
            SocialPosts("1", "Random bluesky post"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    private val bluesky: Bluesky = mockk()
    private val configurationRepository: ConfigurationRepository = mockk()

    private fun buildBluesky(configuration: SocialConfiguration): BlueskyCredentialsValidator {
        every { configurationRepository.find() } returns configuration

        return BlueskyCredentialsValidator(configurationRepository, bluesky)
    }

    @Test
    fun `should handle missing bluesky credentials`() {
        val validator = buildBluesky(SocialConfiguration())

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: bluesky", exception.message)
    }

    @Test
    fun `should handle missing identifier`() {
        val validator = buildBluesky(SocialConfiguration(bluesky = BlueskyCredentials()))

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: identifier", exception.message)
    }

    @Test
    fun `should handle missing password`() {
        val validator =
            buildBluesky(
                SocialConfiguration(bluesky = BlueskyCredentials(identifier = "test.bsky.social")),
            )

        val exception =
            assertFailsWith<MissingConfigurationSetup> {
                validator.send(scheduledPost)
            }

        assertEquals("Missing required configuration: password", exception.message)
    }

    @Test
    fun `should send a post to bluesky`() {
        val published =
            SocialPosts(
                scheduledPost.post.id,
                scheduledPost.post.text,
                "at://did:plc:test/app.bsky.feed.post/test-rkey",
            )
        every { bluesky.publish(any()) } returns published

        val validator =
            buildBluesky(
                SocialConfiguration(
                    bluesky = BlueskyCredentials(identifier = "test.bsky.social", password = "app-password"),
                ),
            )

        validator.send(scheduledPost)

        verify(exactly = 1) { bluesky.publish(scheduledPost.post.text) }
    }
}
