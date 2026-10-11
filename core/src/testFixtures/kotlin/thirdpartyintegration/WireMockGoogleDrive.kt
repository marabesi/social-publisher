package thirdpartyintegration

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.patch
import com.github.tomakehurst.wiremock.client.WireMock.patchRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockGoogleDrive {
    private const val BASE_URL_PROPERTY = "google.drive.api.baseUrl"
    private const val FILE_ID = "test-file-id"
    private const val LIST_PATH = "/drive/v3/files"
    private const val UPLOAD_PATH = "/upload/drive/v3/files"
    private const val UPDATE_PATH = "/upload/drive/v3/files/[^/]+"
    private var server: WireMockServer? = null

    fun start(existingFileNames: List<String> = emptyList()) {
        if (server != null) {
            return
        }

        val wireMock = WireMockServer(wireMockConfig().dynamicPort())
        wireMock.start()

        wireMock.stubFor(
            get(urlPathEqualTo(LIST_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(listBody(existingFileNames)),
                ),
        )

        wireMock.stubFor(
            post(urlPathEqualTo(UPLOAD_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"kind":"drive#file","id":"$FILE_ID","name":"posts-prod.csv"}"""),
                ),
        )

        wireMock.stubFor(
            patch(urlPathMatching(UPDATE_PATH))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"kind":"drive#file","id":"$FILE_ID"}"""),
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

    fun listCalls(): Int = server?.findAll(getRequestedFor(urlPathEqualTo(LIST_PATH)))?.size ?: 0

    fun createCalls(): Int = server?.findAll(postRequestedFor(urlPathEqualTo(UPLOAD_PATH)))?.size ?: 0

    fun updateCalls(): Int = server?.findAll(patchRequestedFor(urlPathMatching(UPDATE_PATH)))?.size ?: 0

    private fun listBody(existingFileNames: List<String>): String {
        val files =
            existingFileNames.mapIndexed { index, name ->
                """{"id":"existing-file-id-$index","name":"$name"}"""
            }
        return """{"files":[${files.joinToString(",")}]}"""
    }
}
