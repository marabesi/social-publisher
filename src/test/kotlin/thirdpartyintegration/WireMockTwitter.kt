package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockTwitter {
    private const val BASE_URL_PROPERTY = "twitter.api.baseUrl"
    private var server: WireMockServer? = null

    fun start() {
        if (server != null) {
            return
        }

        val wireMock = WireMockServer(wireMockConfig().dynamicPort())
        wireMock.start()

        wireMock.stubFor(
            post(urlPathEqualTo("/2/tweets"))
                .willReturn(
                    aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"data":{"id":"1"}}"""),
                ),
        )

        wireMock.stubFor(
            post(urlPathMatching("/1.1/statuses/destroy/.+\\.json"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"id_str":"1"}"""),
                ),
        )

        wireMock.stubFor(
            get(urlPathEqualTo("/1.1/statuses/user_timeline.json"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]"),
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
