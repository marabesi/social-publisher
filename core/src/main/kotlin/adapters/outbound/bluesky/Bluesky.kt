package adapters.outbound.bluesky

import adapters.outbound.CouldNotPublishPostException
import application.entities.BlueskyCredentials
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.PublishPost
import jakarta.inject.Inject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import java.time.Instant
import java.util.logging.Logger

@Serializable
data class BlueskySession(
    @SerialName("accessJwt") val accessJwt: String = "",
    val did: String = "",
)

@Serializable
data class BlueskyRecordCreated(
    val uri: String = "",
)

class Bluesky
    @Inject
    constructor(
        val configurationRepository: ConfigurationRepository,
    ) : PublishPost {
        private val logger = Logger.getLogger(Bluesky::class.java.name)

        override fun publish(text: String): SocialPosts {
            val credentials = configurationRepository.find().bluesky!!

            val session = createSession(credentials)
            val record = createRecord(text, session)

            return SocialPosts(
                id = null,
                text = text,
                socialMediaId = record.uri,
            )
        }

        private fun createSession(credentials: BlueskyCredentials): BlueskySession {
            val request = HttpPost("${BlueskyApi.baseUrl()}/xrpc/com.atproto.server.createSession")
            request.addHeader("Content-Type", "application/json")

            val requestBody =
                buildJsonObject {
                    put("identifier", credentials.identifier)
                    put("password", credentials.password)
                }

            request.entity = StringEntity(requestBody.toString())

            val response = DefaultHttpClient().execute(request)

            val responseBody =
                response.entity.content
                    .bufferedReader()
                    .use { it.readText() }

            logger.info("Bluesky API response: $responseBody")

            if (response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw CouldNotPublishPostException(responseBody + " " + response.allHeaders.contentDeepToString())
            }

            return JSON.decodeFromString<BlueskySession>(responseBody)
        }

        private fun createRecord(
            text: String,
            session: BlueskySession,
        ): BlueskyRecordCreated {
            val request = HttpPost("${BlueskyApi.baseUrl()}/xrpc/com.atproto.repo.createRecord")
            request.addHeader("Content-Type", "application/json")
            request.addHeader("Authorization", "Bearer ${session.accessJwt}")

            val requestBody =
                buildJsonObject {
                    put("repo", session.did)
                    put("collection", "app.bsky.feed.post")
                    putJsonObject("record") {
                        put("\$type", "app.bsky.feed.post")
                        put("text", text)
                        put("createdAt", Instant.now().toString())
                    }
                }

            request.entity = StringEntity(requestBody.toString())

            val response = DefaultHttpClient().execute(request)

            val responseBody =
                response.entity.content
                    .bufferedReader()
                    .use { it.readText() }

            logger.info("Bluesky API response: $responseBody")

            if (response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw CouldNotPublishPostException(responseBody + " " + response.allHeaders.contentDeepToString())
            }

            return JSON.decodeFromString<BlueskyRecordCreated>(responseBody)
        }

        companion object {
            private val JSON = Json { ignoreUnknownKeys = true }
        }
    }
