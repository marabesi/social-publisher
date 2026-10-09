package adapters.outbound.social

import application.entities.LinkedInCredentials
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.ExchangeLinkedInAuthorization
import application.socialnetwork.LinkedInToken
import jakarta.inject.Inject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import java.nio.charset.StandardCharsets

@Serializable
private data class AccessTokenResponse(
    @SerialName("access_token") val accessToken: String = "",
)

@Serializable
private data class UserInfo(
    val sub: String = "",
)

class LinkedInOAuthClient
    @Inject
    constructor(
        val configurationRepository: ConfigurationRepository,
    ) : ExchangeLinkedInAuthorization {
        override fun exchange(code: String): LinkedInToken {
            val linkedin = configurationRepository.find().linkedin!!
            val accessToken = requestAccessToken(code, linkedin)

            return LinkedInToken(
                accessToken = accessToken,
                authorUrn = fetchAuthorUrn(accessToken),
            )
        }

        private fun requestAccessToken(
            code: String,
            linkedin: LinkedInCredentials,
        ): String {
            val request = HttpPost("${LinkedinApi.oauthBaseUrl()}/oauth/v2/accessToken")
            request.entity =
                UrlEncodedFormEntity(
                    listOf(
                        BasicNameValuePair("grant_type", "authorization_code"),
                        BasicNameValuePair("code", code),
                        BasicNameValuePair("redirect_uri", linkedin.redirectUri),
                        BasicNameValuePair("client_id", linkedin.clientId),
                        BasicNameValuePair("client_secret", linkedin.clientSecret),
                    ),
                    StandardCharsets.UTF_8,
                )

            val response = DefaultHttpClient().execute(request)
            val body =
                response.entity.content
                    .bufferedReader()
                    .use { it.readText() }

            if (response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw CouldNotConnectLinkedInException(body + " " + response.allHeaders.contentDeepToString())
            }

            return JSON.decodeFromString<AccessTokenResponse>(body).accessToken
        }

        private fun fetchAuthorUrn(accessToken: String): String {
            val request = HttpGet("${LinkedinApi.baseUrl()}/v2/userinfo")
            request.addHeader("Authorization", "Bearer $accessToken")

            val response = DefaultHttpClient().execute(request)
            val body =
                response.entity.content
                    .bufferedReader()
                    .use { it.readText() }

            if (response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw CouldNotConnectLinkedInException(body + " " + response.allHeaders.contentDeepToString())
            }

            val sub = JSON.decodeFromString<UserInfo>(body).sub
            return "urn:li:person:$sub"
        }

        companion object {
            private val JSON = Json { ignoreUnknownKeys = true }
        }
    }
