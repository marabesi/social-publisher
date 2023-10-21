package adapters.outbound.social

import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.DeleteTweet
import org.springframework.social.twitter.api.impl.TwitterTemplate

class DeleteTweet(val configurationRepository: ConfigurationRepository) : DeleteTweet {
    private val configuration = configurationRepository.find()
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
