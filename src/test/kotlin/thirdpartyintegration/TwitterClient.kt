package thirdpartyintegration

import adapters.outbound.social.DeleteTweet
import adapters.outbound.social.Twitter
import adapters.outbound.social.TwitterCredentialsValidator
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import application.persistence.configuration.ConfigurationRepository
import io.github.cdimascio.dotenv.Dotenv
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertNotNull

class TwitterClient {
    private val dotenv = Dotenv
        .configure()
        .systemProperties()
        .load()
    private lateinit var credentials: TwitterCredentials
    private val configurationRepository: ConfigurationRepository = mockk()

    private val scheduledPost = ScheduledItem(
        SocialPosts("1", "Random tweet 123"),
        Instant.parse("2014-12-22T10:15:30Z")
    )

    @BeforeEach
    fun setUp() {
        credentials = TwitterCredentials(
            dotenv["TWITTER_CONSUMER_KEY"],
            dotenv["TWITTER_CONSUMER_SECRET"],
            dotenv["TWITTER_TOKEN"],
            dotenv["TWITTER_TOKEN_SECRET"],
        )
    }

    @Test
    fun `should send a tweet to twitter through social spring integration`() {
        val config = SocialConfiguration(
            twitter = credentials
        )

        every { configurationRepository.find() } returns config

        val twitter = TwitterCredentialsValidator(
            configurationRepository,
            Twitter(configurationRepository)
        )

        val tweet = twitter.send(scheduledPost)

        assertNotNull(tweet.socialMediaId)

        val deleteTweet = DeleteTweet(configurationRepository)
        deleteTweet.deleteTweetByTweetId(tweet.socialMediaId.toString())
    }
}
