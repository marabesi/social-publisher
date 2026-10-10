package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.patch
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockNotion {
    private const val BASE_URL_PROPERTY = "notion.api.baseUrl"
    private const val UPLOAD_ID = "test-upload-id"
    private var server: WireMockServer? = null

    fun start() {
        if (server != null) {
            return
        }

        val wireMock = WireMockServer(wireMockConfig().dynamicPort())
        wireMock.start()

        wireMock.stubFor(
            post(urlPathEqualTo("/v1/file_uploads"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"file_upload","id":"$UPLOAD_ID","status":"pending"}"""),
                ),
        )

        wireMock.stubFor(
            post(urlPathEqualTo("/v1/file_uploads/$UPLOAD_ID/send"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"file_upload","id":"$UPLOAD_ID","status":"uploaded"}"""),
                ),
        )

        wireMock.stubFor(
            patch(urlPathMatching("/v1/blocks/.+/children"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"list"}"""),
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
