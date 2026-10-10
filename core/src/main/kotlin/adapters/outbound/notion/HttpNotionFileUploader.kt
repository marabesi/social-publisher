package adapters.outbound.notion

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClients
import java.io.File

class HttpNotionFileUploader(
    private val apiKey: String,
    private val pageId: String = "",
    private val baseUrl: String = NotionApi.baseUrl(),
) : NotionFileUploader {
    private val httpClient = HttpClients.createDefault()

    override fun upload(file: File): String {
        val uploadId = createUpload(file)
        sendContent(uploadId, file)

        if (pageId.isNotBlank()) {
            attach(uploadId, file.name)
        }

        return uploadId
    }

    private fun createUpload(file: File): String {
        val request = authorised(HttpPost("$baseUrl/v1/file_uploads"))
        request.addHeader("Content-Type", "application/json")
        request.entity =
            StringEntity(
                buildJsonObject {
                    put("mode", "single_part")
                    put("filename", file.name)
                    put("content_type", OCTET_STREAM)
                }.toString(),
                ContentType.APPLICATION_JSON,
            )

        val body = execute(request)
        val uploadId =
            Json
                .parseToJsonElement(body)
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content
        return uploadId
    }

    private fun sendContent(
        uploadId: String,
        file: File,
    ) {
        val request = authorised(HttpPost("$baseUrl/v1/file_uploads/$uploadId/send"))
        request.entity =
            MultipartEntityBuilder
                .create()
                .addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.name)
                .build()

        execute(request)
    }

    private fun attach(
        uploadId: String,
        fileName: String,
    ) {
        val request = authorised(HttpPatch("$baseUrl/v1/blocks/$pageId/children"))
        request.addHeader("Content-Type", "application/json")
        request.entity =
            StringEntity(
                buildJsonObject {
                    put(
                        "children",
                        buildJsonArray {
                            add(
                                buildJsonObject {
                                    put("object", "block")
                                    put("type", "file")
                                    put(
                                        "file",
                                        buildJsonObject {
                                            put("type", "file_upload")
                                            put(
                                                "file_upload",
                                                buildJsonObject { put("id", uploadId) },
                                            )
                                            put("name", fileName)
                                        },
                                    )
                                },
                            )
                        },
                    )
                }.toString(),
                ContentType.APPLICATION_JSON,
            )

        execute(request)
    }

    private fun authorised(request: HttpEntityEnclosingRequestBase): HttpEntityEnclosingRequestBase {
        request.addHeader("Authorization", "Bearer $apiKey")
        request.addHeader(NotionApi.VERSION_HEADER, NotionApi.VERSION)
        return request
    }

    private fun execute(request: HttpUriRequest): String =
        httpClient.execute(request).use { response ->
            val body =
                response.entity
                    ?.content
                    ?.bufferedReader()
                    ?.use { it.readText() }
                    .orEmpty()

            val status = response.statusLine.statusCode
            if (status !in SUCCESS_RANGE) {
                throw CouldNotUploadToNotionException("Notion API returned $status: $body")
            }

            body
        }

    private companion object {
        const val OCTET_STREAM = "application/octet-stream"
        val SUCCESS_RANGE = 200..299
    }
}
