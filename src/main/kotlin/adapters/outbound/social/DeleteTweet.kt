package adapters.outbound.social

import application.socialnetwork.DeleteTweet
import application.entities.SocialConfiguration
import org.springframework.social.twitter.api.impl.TwitterTemplate

class DeleteTweet(val configuration: SocialConfiguration): DeleteTweet {
    private val client = TwitterTemplate(
        configuration.twitter!!.consumerKey,
        configuration.twitter!!.consumerSecret,
        configuration.twitter!!.accessToken,
        configuration.twitter!!.accessTokenSecret
    )

    override fun deleteTweetByTweetText(text: String): Boolean {
        client.timelineOperations().userTimeline.forEach {
            if (it.text.equals(text)) {
                client.timelineOperations().deleteStatus(it.id)
                return true
            }
        }

        return false
    }

    override fun deleteTweetByTweetId(id: String): Boolean {
        client.timelineOperations().deleteStatus(id.toLong())
        return true
    }
}