package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockBluesky {
    private const val BASE_URL_PROPERTY = "bluesky.api.baseUrl"
    private var server: WireMockServer? = null

    fun start() {
        if (server != null) {
            return
        }

        val wireMock = WireMockServer(wireMockConfig().dynamicPort())
        wireMock.start()

        wireMock.stubFor(
            post(urlPathEqualTo("/xrpc/com.atproto.server.createSession"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """{"accessJwt":"test-access-jwt","refreshJwt":"refresh-jwt","handle":"test.bsky.social","did":"did:plc:test"}""",
                        ),
                ),
        )

        wireMock.stubFor(
            post(urlPathEqualTo("/xrpc/com.atproto.repo.createRecord"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """{"uri":"at://did:plc:test/app.bsky.feed.post/test-rkey","cid":"bafyrei-test"}""",
                        ),
                ),
        )

        System.setProperty(BASE_URL_PROPERTY, wireMock.baseUrl())
        server = wireMock
    }

    fun stop() {
        server?.stop()
        server = null
        System.clearProperty(BASE_URL_PROPERTY)
    }
}
