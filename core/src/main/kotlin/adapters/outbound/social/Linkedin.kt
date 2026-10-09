package adapters.outbound.social

import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.PublishPost
import jakarta.inject.Inject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient

class Linkedin
    @Inject
    constructor(
        val configurationRepository: ConfigurationRepository,
    ) : PublishPost {
        override fun publish(text: String): SocialPosts {
            val credentials = configurationRepository.find().linkedin!!

            val request = HttpPost("${LinkedinApi.baseUrl()}/v2/ugcPosts")
            request.addHeader("Content-Type", "application/json")
            request.addHeader("X-Restli-Protocol-Version", "2.0.0")
            request.addHeader("Authorization", "Bearer ${credentials.accessToken}")

            val requestBody =
                buildJsonObject {
                    put("author", credentials.authorUrn)
                    put("lifecycleState", "PUBLISHED")
                    putJsonObject("specificContent") {
                        putJsonObject("com.linkedin.ugc.ShareContent") {
                            putJsonObject("shareCommentary") { put("text", text) }
                            put("shareMediaCategory", "NONE")
                        }
                    }
                    putJsonObject("visibility") {
                        put("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC")
                    }
                }

            request.entity = StringEntity(requestBody.toString())

            val response = DefaultHttpClient().execute(request)

            val responseBody =
                response.entity.content
                    .bufferedReader()
                    .use { it.readText() }

            if (response.statusLine.statusCode != HttpStatus.SC_CREATED) {
                throw CouldNotPublishPostException(responseBody + " " + response.allHeaders.contentDeepToString())
            }

            return SocialPosts(
                id = null,
                text = text,
                socialMediaId = response.getFirstHeader("x-restli-id")?.value,
            )
        }
    }
