package adapters.outbound.social

import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.entities.TwitterCredentials
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.CreateTweet
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.SocialThirdParty
import jakarta.inject.Inject

open class TwitterCredentialsValidator
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
        private val createTweet: CreateTweet,
    ) : SocialThirdParty {
        @TweetCreated
        override fun send(scheduledItem: ScheduledItem): SocialPosts {
            val configuration = configurationRepository.find()
            validate(configuration.twitter)

            return createTweet.sendTweet(scheduledItem.post.text)
        }

        private fun validate(twitter: TwitterCredentials?) {
            val missingParameter = firstMissingParameter(twitter)

            if (missingParameter != null) {
                throw MissingConfigurationSetup(missingParameter)
            }
        }

        private fun firstMissingParameter(twitter: TwitterCredentials?): String? =
            when {
                twitter == null -> "twitter"
                twitter.consumerKey.isEmpty() -> "consumer key"
                twitter.consumerSecret.isEmpty() -> "consumer secret"
                twitter.accessToken.isEmpty() -> "access token"
                twitter.accessTokenSecret.isEmpty() -> "token secret"
                else -> null
            }
    }
