package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockLinkedIn {
    private const val API_BASE_URL_PROPERTY = "linkedin.api.baseUrl"
    private const val OAUTH_BASE_URL_PROPERTY = "linkedin.oauth.baseUrl"
    private var server: WireMockServer? = null

    fun start() {
        if (server != null) {
            return
        }

        val wireMock = WireMockServer(wireMockConfig().dynamicPort())
        wireMock.start()

        wireMock.stubFor(
            post(urlPathEqualTo("/v2/ugcPosts"))
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("x-restli-id", "urn:li:share:1")
                        .withBody("""{"id":"urn:li:share:1"}"""),
                ),
        )

        wireMock.stubFor(
            post(urlPathEqualTo("/oauth/v2/accessToken"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"access_token":"test-linkedin-access-token","expires_in":5184000}"""),
                ),
        )

        wireMock.stubFor(
            get(urlPathEqualTo("/v2/userinfo"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"sub":"test-member-id"}"""),
                ),
        )

        System.setProperty(API_BASE_URL_PROPERTY, wireMock.baseUrl())
        System.setProperty(OAUTH_BASE_URL_PROPERTY, wireMock.baseUrl())
        server = wireMock
    }

    fun stop() {
        server?.stop()
        server = null
        System.clearProperty(API_BASE_URL_PROPERTY)
        System.clearProperty(OAUTH_BASE_URL_PROPERTY)
    }
}
