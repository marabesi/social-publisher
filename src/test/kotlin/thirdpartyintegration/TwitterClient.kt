package thirdpartyintegration

import adapters.outbound.social.SocialSpringTwitterClient
import adapters.outbound.social.TwitterIntegration
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import io.github.cdimascio.dotenv.dotenv
import org.junit.jupiter.api.Test
import org.springframework.social.twitter.api.impl.TwitterTemplate
import java.time.Instant
import kotlin.test.assertNotNull

class TwitterClient {

    private val scheduledPost = ScheduledItem(
        SocialPosts("1", "Random tweet 123"),
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

        val tweet = twitter.send(scheduledPost)

        assertNotNull(tweet.socialMediaId)

        val client= TwitterTemplate(
            dotenv["TWITTER_CONSUMER_KEY"],
            dotenv["TWITTER_CONSUMER_SECRET"],
            dotenv["TWITTER_TOKEN"],
            dotenv["TWITTER_TOKEN_SECRET"],
        )

        client.timelineOperations().deleteStatus(tweet.socialMediaId!!.toLong())
    }
}