package adapters.outbound.social

import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.socialnetwork.CreateTweet
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import oauth.signpost.OAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

data class Tweet(val id: String)

data class TweetCreatedResponse(val data: Tweet)

class Twitter(
    val configuration: SocialConfiguration
) : CreateTweet {
    private val createTweetEndpoint = "https://api.twitter.com/2/tweets"

    override fun sendTweet(text: String): SocialPosts {
        val consumerKey = configuration.twitter!!.consumerKey
        val consumerSecret = configuration.twitter!!.consumerSecret
        val consumer: OAuthConsumer = CommonsHttpOAuthConsumer(consumerKey, consumerSecret)
        val token = configuration.twitter!!.accessToken
        val tokenSecret = configuration.twitter!!.accessTokenSecret

        consumer.setTokenWithSecret(token, tokenSecret)

        val postRequest = HttpPost(createTweetEndpoint)
        postRequest.addHeader("Content-Type", "application/json")

        consumer.sign(postRequest)

        val requestBody = buildJsonObject { put("text", text) }

        postRequest.entity = StringEntity(requestBody.toString())

        val httpClient = DefaultHttpClient()
        val response = httpClient.execute(postRequest)

        val responseBody = response.entity.content.bufferedReader().use { it.readText() }
        val jsonData = Json.parseToJsonElement(responseBody)

        val rawData = jsonData.jsonObject["data"]
        val tweet = TweetCreatedResponse(Tweet(id = rawData!!.jsonObject["id"].toString().replace("\"", "")))

        return SocialPosts(
            id = null,
            text = text,
            socialMediaId = tweet.data.id
        )
    }
}
