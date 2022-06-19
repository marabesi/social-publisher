package thirdpartyintegration

import adapters.outbound.social.SocialSpringTwitterClient
import adapters.outbound.social.TwitterIntegration
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertTrue
import io.github.cdimascio.dotenv.dotenv

class TwitterClient {

    private val scheduledPost = ScheduledItem(
        SocialPosts(1, "Random tweet"),
        Instant.parse("2014-12-22T10:15:30Z")
    )

    @Test
    fun `should send a tweet to twitter through social spring integration`() {
        val dotenv = dotenv()

        val config = SocialConfiguration(
            twitter = TwitterCredentials(
                dotenv["TWITTER_CONSUMER_KEY"],
                dotenv["TWITTER_CONSUMER_SECRET"],
                dotenv["TWITTER_TOKEN"],
                dotenv["TWITTER_TOKEN_SECRET"],
            )
        )

        val twitter = TwitterIntegration(
            config,
            SocialSpringTwitterClient(config)
        )

        assertTrue(
            twitter.send(scheduledPost)
        )
    }
}