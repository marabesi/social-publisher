package adapters.outbound.social

import application.socialnetwork.DeleteTweet
import application.entities.SocialConfiguration
import org.springframework.social.twitter.api.impl.TwitterTemplate

class DeleteTweet(val configuration: SocialConfiguration): DeleteTweet {
    override fun deleteTweet(text: String): Boolean {
        val client = TwitterTemplate(
            configuration.twitter!!.consumerKey,
            configuration.twitter!!.consumerSecret,
            configuration.twitter!!.accessToken,
            configuration.twitter!!.accessTokenSecret
        )

        client.timelineOperations().userTimeline.forEach {
            if (it.text.equals(text)) {
                client.timelineOperations().deleteStatus(it.id)
                return true
            }
        }

        return false
    }
}