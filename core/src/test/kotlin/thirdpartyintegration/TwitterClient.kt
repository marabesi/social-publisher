package thirdpartyintegration

import adapters.outbound.twitter.DeleteTweet
import adapters.outbound.twitter.Twitter
import adapters.outbound.twitter.TwitterCredentialsValidator
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import application.persistence.configuration.ConfigurationRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.test.assertContains
import kotlin.test.assertNotNull

class TwitterClient {
    private lateinit var credentials: TwitterCredentials
    private val configurationRepository: ConfigurationRepository = mockk()

    private val scheduledPost =
        ScheduledItem(
            SocialPosts("1", "Random tweet 123"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    @BeforeEach
    fun setUp() {
        WireMockTwitter.start()
        credentials =
            TwitterCredentials(
                "test-consumer-key",
                "test-consumer-secret",
                "test-access-token",
                "test-access-token-secret",
            )
    }

    @AfterEach
    fun tearDown() {
        WireMockTwitter.stop()
    }

    @Test
    fun `should send and delete a tweet through the twitter api`() {
        val config =
            SocialConfiguration(
                twitter = credentials,
            )

        every { configurationRepository.find() } returns config

        val twitter =
            TwitterCredentialsValidator(
                configurationRepository,
                Twitter(configurationRepository),
            )

        val tweet = twitter.send(scheduledPost)

        assertNotNull(tweet.socialMediaId)

        val deleteTweet = DeleteTweet(configurationRepository)
        deleteTweet.deleteTweetByTweetId(tweet.socialMediaId.toString())
    }

    @Test
    fun `should log the full response from the twitter api`() {
        val config =
            SocialConfiguration(
                twitter = credentials,
            )

        every { configurationRepository.find() } returns config

        val twitter = Twitter(configurationRepository)
        val logger = Logger.getLogger(Twitter::class.java.name)
        val records = mutableListOf<LogRecord>()
        val handler =
            object : Handler() {
                override fun publish(record: LogRecord) {
                    records.add(record)
                }

                override fun flush() = Unit

                override fun close() = Unit
            }
        logger.addHandler(handler)

        try {
            twitter.publish("Random tweet 123")
        } finally {
            logger.removeHandler(handler)
        }

        val logged = records.singleOrNull { it.level == Level.INFO }?.message ?: ""
        assertContains(logged, """{"data":{"id":"1"}}""")
    }
}
