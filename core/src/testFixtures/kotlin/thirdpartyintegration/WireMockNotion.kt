package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.patch
import com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockNotion {
    private const val BASE_URL_PROPERTY = "notion.api.baseUrl"
    private const val UPLOAD_ID = "test-upload-id"
    private const val BLOCK_ID = "existing-block-id"
    private const val CHILDREN_PATH = "/v1/blocks/[^/]+/children"
    private const val BLOCK_PATH = "/v1/blocks/[^/]+"
    private var server: WireMockServer? = null

    fun start(existingFileNames: List<String> = emptyList()) {
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
            get(urlPathMatching(CHILDREN_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(childrenBody(existingFileNames)),
                ),
        )

        wireMock.stubFor(
            patch(urlPathMatching(CHILDREN_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"list"}"""),
                ),
        )

        wireMock.stubFor(
            patch(urlPathMatching(BLOCK_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"block"}"""),
                ),
        )

        wireMock.stubFor(
            delete(urlPathMatching(BLOCK_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"object":"block"}"""),
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

    fun updateBlockCalls(): Int = server?.findAll(patchRequestedFor(urlPathMatching(BLOCK_PATH)))?.size ?: 0

    fun appendBlockCalls(): Int = server?.findAll(patchRequestedFor(urlPathMatching(CHILDREN_PATH)))?.size ?: 0

    fun deleteBlockCalls(): Int = server?.findAll(deleteRequestedFor(urlPathMatching(BLOCK_PATH)))?.size ?: 0

    private fun childrenBody(existingFileNames: List<String>): String {
        val results =
            existingFileNames.mapIndexed { index, name ->
                """{"object":"block","id":"$BLOCK_ID-$index","type":"file",""" +
                    """"file":{"type":"file","name":"$name","file":{"url":"https://example.com/$name"}}}"""
            }
        return """{"object":"list","results":[${results.joinToString(",")}],"has_more":false,"next_cursor":null}"""
    }
}
