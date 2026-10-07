package adapters.outbound.social

import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.DeleteTweet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.http.HttpRequest
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient

class DeleteTweet(val configurationRepository: ConfigurationRepository) : DeleteTweet {
    override fun deleteTweetByTweetText(text: String): Boolean {
        val tweet = userTimeline().firstOrNull { it.second == text } ?: return false
        return deleteTweetByTweetId(tweet.first)
    }

    override fun deleteTweetByTweetId(id: String): Boolean {
        val request = HttpPost("${TwitterApi.baseUrl()}/1.1/statuses/destroy/$id.json")
        sign(request)

        val response = DefaultHttpClient().execute(request)
        response.entity.content.bufferedReader().use { it.readText() }

        return response.statusLine.statusCode == HttpStatus.SC_OK
    }

    private fun userTimeline(): List<Pair<String, String>> {
        val request = HttpGet("${TwitterApi.baseUrl()}/1.1/statuses/user_timeline.json")
        sign(request)

        val response = DefaultHttpClient().execute(request)
        val body = response.entity.content.bufferedReader().use { it.readText() }

        return Json.parseToJsonElement(body).jsonArray.map {
            val tweet = it.jsonObject
            val id = (tweet["id_str"] ?: tweet["id"]!!).jsonPrimitive.content
            id to tweet["text"]!!.jsonPrimitive.content
        }
    }

    private fun sign(request: HttpRequest) {
        val configuration = configurationRepository.find()
        val consumer =
            CommonsHttpOAuthConsumer(
                configuration.twitter!!.consumerKey,
                configuration.twitter!!.consumerSecret,
            )
        consumer.setTokenWithSecret(
            configuration.twitter!!.accessToken,
            configuration.twitter!!.accessTokenSecret,
        )
        consumer.sign(request)
    }
}
