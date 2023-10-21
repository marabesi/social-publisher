package adapters.outbound.social

import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.CreateTweet
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.SocialThirdParty
import com.google.inject.Inject

open class TwitterCredentialsValidator @Inject constructor(
    private val configurationRepository: ConfigurationRepository,
    private val createTweet: CreateTweet
) : SocialThirdParty {
    @TweetCreated
    override fun send(scheduledItem: ScheduledItem): SocialPosts {
        val configuration = configurationRepository.find()

        if (configuration.twitter == null) {
            throw MissingConfigurationSetup("twitter")
        }

        if (configuration.twitter!!.consumerKey.isEmpty()) {
            throw MissingConfigurationSetup("consumer key")
        }

        if (configuration.twitter!!.consumerSecret.isEmpty()) {
            throw MissingConfigurationSetup("consumer secret")
        }

        if (configuration.twitter!!.accessToken.isEmpty()) {
            throw MissingConfigurationSetup("access token")
        }

        if (configuration.twitter!!.accessTokenSecret.isEmpty()) {
            throw MissingConfigurationSetup("token secret")
        }

        return createTweet.sendTweet(scheduledItem.post.text)
    }
}
