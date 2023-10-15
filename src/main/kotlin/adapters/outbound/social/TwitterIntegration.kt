package adapters.outbound.social

import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.SocialThirdParty
import application.socialnetwork.CreateTweet

class TwitterIntegration(
    private val configuration: SocialConfiguration,
    private val createTweet: CreateTweet
) : SocialThirdParty {
    override fun send(scheduledItem: ScheduledItem): SocialPosts {
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
