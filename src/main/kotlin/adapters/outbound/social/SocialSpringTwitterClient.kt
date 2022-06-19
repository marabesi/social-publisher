package adapters.outbound.social

import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.socialnetwork.TwitterClient
import org.springframework.social.twitter.api.impl.TwitterTemplate

class SocialSpringTwitterClient(
    private val configuration: SocialConfiguration,
): TwitterClient {
    override fun sendTweet(text: String): SocialPosts {
        val twitter = TwitterTemplate(
            configuration.twitter!!.consumerKey,
            configuration.twitter!!.consumerSecret,
            configuration.twitter!!.accessToken,
            configuration.twitter!!.accessTokenSecret,
        )

        val tweet = twitter.timelineOperations().updateStatus(text)
        return SocialPosts(
            id = null,
            text = text,
            socialMediaId = tweet.id.toString()
        )
    }
}