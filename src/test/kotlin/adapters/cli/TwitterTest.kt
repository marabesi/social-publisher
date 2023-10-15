package adapters.cli

import adapters.outbound.social.TwitterIntegration
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.CreateTweet
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TwitterTest {

    private val scheduledPost = ScheduledItem(
        SocialPosts("1", "Random tweet"),
        Instant.parse("2014-12-22T10:15:30Z")
    )

    private val socialIntegration: CreateTweet = mockk()

    private fun buildTwitter(configuration: SocialConfiguration, createTweet: CreateTweet): TwitterIntegration {
        return TwitterIntegration(configuration, createTweet)
    }

    @Test
    fun `should handle missing twitter credentials`() {
        val twitter = buildTwitter(SocialConfiguration(), socialIntegration)

        val exception = assertFailsWith<MissingConfigurationSetup>(
            block = {
                twitter.send(scheduledPost)
            }
        )

        assertEquals("Missing required configuration: twitter", exception.message)
    }

    @Test
    fun `should handle unauthorized attempt of sending a tweet without consumer key`() {
        val twitter = buildTwitter(SocialConfiguration(twitter = TwitterCredentials()), socialIntegration)

        val exception = assertFailsWith<MissingConfigurationSetup>(
            block = {
                twitter.send(scheduledPost)
            }
        )

        assertEquals("Missing required configuration: consumer key", exception.message)
    }

    @Test
    fun `should handle unauthorized attempt of sending a tweet without consumer secret`() {
        val twitter = buildTwitter(
            SocialConfiguration(twitter = TwitterCredentials("123")),
            socialIntegration
        )

        val exception = assertFailsWith<MissingConfigurationSetup>(
            block = {
                twitter.send(scheduledPost)
            }
        )

        assertEquals("Missing required configuration: consumer secret", exception.message)
    }

    @Test
    fun `should handle unauthorized attempt of sending a tweet without access token`() {
        val twitter = buildTwitter(
            SocialConfiguration(twitter = TwitterCredentials("123", "123")),
            socialIntegration
        )

        val exception = assertFailsWith<MissingConfigurationSetup>(
            block = {
                twitter.send(scheduledPost)
            }
        )

        assertEquals("Missing required configuration: access token", exception.message)
    }

    @Test
    fun `should handle unauthorized attempt of sending a tweet without token secret`() {
        val twitter = buildTwitter(
            SocialConfiguration(
                twitter = TwitterCredentials("123", "123", "123")
            ),
            socialIntegration
        )

        val exception = assertFailsWith<MissingConfigurationSetup>(
            block = {
                twitter.send(scheduledPost)
            }
        )

        assertEquals("Missing required configuration: token secret", exception.message)
    }

    @Test
    fun `should send a tweet to twitter api`() {
        val tweet = SocialPosts(
            scheduledPost.post.id,
            scheduledPost.post.text,
            "twitter-id"
        )
        every { socialIntegration.sendTweet(any()) } returns tweet

        val twitter = buildTwitter(
            SocialConfiguration(
                twitter = TwitterCredentials(
                    "123",
                    "123",
                    "123",
                    "123"
                )
            ),
            socialIntegration
        )

        twitter.send(scheduledPost)

        verify(exactly = 1) { socialIntegration.sendTweet(scheduledPost.post.text) }
    }
}
