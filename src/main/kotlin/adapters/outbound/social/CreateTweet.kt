package adapters.outbound.social

import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.CreateTweet
import com.google.inject.Inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import oauth.signpost.OAuthConsumer
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer
import org.aopalliance.intercept.MethodInterceptor
import org.aopalliance.intercept.MethodInvocation
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TweetCreated

class TweetCreatedInterceptor(
    private val currentConfiguration: ConfigurationRepository,
    private val isInTestMode: Boolean
) : MethodInterceptor {
    override fun invoke(invocation: MethodInvocation?): SocialPosts {
        val result = invocation!!.proceed() as SocialPosts

        if (isInTestMode) {
            DeleteTweet(currentConfiguration).deleteTweetByTweetId(result.socialMediaId!!)
        }

        return result
    }
}

data class Tweet(val id: String)

data class TweetCreatedResponse(val data: Tweet)

class Twitter @Inject constructor(
    val configurationRepository: ConfigurationRepository
) : CreateTweet {
    private val createTweetEndpoint = "https://api.twitter.com/2/tweets"

    override fun sendTweet(text: String): SocialPosts {
        val configuration = configurationRepository.find()
        val consumerKey = configuration.twitter!!.consumerKey
        val consumerSecret = configuration.twitter!!.consumerSecret
        val token = configuration.twitter!!.accessToken
        val tokenSecret = configuration.twitter!!.accessTokenSecret

        val consumer: OAuthConsumer = CommonsHttpOAuthConsumer(consumerKey, consumerSecret)
        consumer.setTokenWithSecret(token, tokenSecret)

        val postRequest = HttpPost(createTweetEndpoint)
        postRequest.addHeader("Content-Type", "application/json")

        consumer.sign(postRequest)

        val requestBody = buildJsonObject { put("text", text) }

        postRequest.entity = StringEntity(requestBody.toString())

        val httpClient = DefaultHttpClient()
        val response = httpClient.execute(postRequest)

        val responseBody = response.entity.content.bufferedReader().use { it.readText() }

        if (response.statusLine.statusCode != HttpStatus.SC_CREATED) {
            throw CouldNotCreateTweetException(responseBody + " " + response.allHeaders.contentDeepToString())
        }

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
