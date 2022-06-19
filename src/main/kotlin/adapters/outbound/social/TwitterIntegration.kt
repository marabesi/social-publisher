package adapters.outbound.social

import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.TwitterClient
import application.socialnetwork.SocialThirdParty


class TwitterIntegration(
    private val configuration: SocialConfiguration,
    private val twitterClient: TwitterClient
) : SocialThirdParty {
    override fun send(scheduledItem: ScheduledItem): Boolean {
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

        twitterClient.sendTweet(scheduledItem.post.text)
        return true
    }
}