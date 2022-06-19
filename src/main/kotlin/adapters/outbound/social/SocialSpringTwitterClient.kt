package adapters.outbound.social

import application.entities.SocialConfiguration
import application.socialnetwork.TwitterClient
import org.springframework.social.twitter.api.impl.TwitterTemplate

class SocialSpringTwitterClient(
    private val configuration: SocialConfiguration,
): TwitterClient {
    override fun sendTweet(text: String) {
        val twitter = TwitterTemplate(
            configuration.twitter!!.consumerKey,
            configuration.twitter!!.consumerSecret,
            configuration.twitter!!.accessToken,
            configuration.twitter!!.accessTokenSecret,
        )

        twitter.timelineOperations().updateStatus(text)
    }
}