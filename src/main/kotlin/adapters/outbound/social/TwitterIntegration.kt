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

//        val twitter = TwitterTemplate(
//            "AmONAp9GSQw6wcQG7MCZcRmT3",
//            "rPI1IXsmKTA0DqKhXw6p3Q9sabcqJntbDMlIHhO4mZX8MxiBFA",
//            "1537729846750679042-069q2NBGaL3mRXtBXHhTHGFbvEZZD5",
//            "LKG3lZENbGpjePa3Hz5Mb3FNSfCrbmsLCtV4gqSvwVV65"
//        )

        twitterClient.sendTweet(scheduledItem.post.text)
//        twitter.timelineOperations().updateStatus("Spring Social is awesome!")
        return true
    }
}