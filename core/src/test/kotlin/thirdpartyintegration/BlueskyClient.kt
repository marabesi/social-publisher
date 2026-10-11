package thirdpartyintegration

import adapters.outbound.bluesky.Bluesky
import adapters.outbound.bluesky.BlueskyCredentialsValidator
import application.entities.BlueskyCredentials
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

class BlueskyClient {
    private val configurationRepository: ConfigurationRepository = mockk()

    private val scheduledPost =
        ScheduledItem(
            SocialPosts("1", "Random bluesky post"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    @BeforeEach
    fun setUp() {
        WireMockBluesky.start()
    }

    @AfterEach
    fun tearDown() {
        WireMockBluesky.stop()
    }

    @Test
    fun `should send a post through the bluesky api`() {
        every { configurationRepository.find() } returns
            SocialConfiguration(
                bluesky = BlueskyCredentials(identifier = "test.bsky.social", password = "test-app-password"),
            )

        val bluesky =
            BlueskyCredentialsValidator(
                configurationRepository,
                Bluesky(configurationRepository),
            )

        val post = bluesky.send(scheduledPost)

        assertNotNull(post.socialMediaId)
        assertEquals("at://did:plc:test/app.bsky.feed.post/test-rkey", post.socialMediaId)
    }
}
