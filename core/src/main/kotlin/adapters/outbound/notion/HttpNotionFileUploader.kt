package adapters.outbound.notion

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.apache.http.client.methods.HttpDelete
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
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
            replaceFileBlock(uploadId, file.name)
        }

        return uploadId
    }

    private fun replaceFileBlock(
        uploadId: String,
        fileName: String,
    ) {
        val existingBlocks = findFileBlocks(fileName)
        val primary = existingBlocks.firstOrNull()
        if (primary == null) {
            attach(uploadId, fileName)
            return
        }

        updateFileBlock(primary, uploadId, fileName)
        existingBlocks.drop(1).forEach(::deleteBlock)
    }

    private fun findFileBlocks(fileName: String): List<String> {
        val blockIds = mutableListOf<String>()
        var cursor: String? = null
        do {
            val url =
                buildString {
                    append("$baseUrl/v1/blocks/$pageId/children?page_size=$PAGE_SIZE")
                    if (cursor != null) {
                        append("&start_cursor=").append(cursor)
                    }
                }
            val page = Json.parseToJsonElement(execute(authorised(HttpGet(url)))).jsonObject
            page["results"]
                ?.jsonArray
                ?.map { it.jsonObject }
                ?.filter { it.fileBlockName() == fileName }
                ?.mapNotNullTo(blockIds) { it["id"]?.jsonPrimitive?.contentOrNull }
            cursor = page["next_cursor"]?.jsonPrimitive?.contentOrNull
        } while (cursor != null)
        return blockIds
    }

    private fun JsonObject.fileBlockName(): String? =
        if (this["type"]?.jsonPrimitive?.contentOrNull == FILE_TYPE) {
            this[FILE_TYPE]
                ?.jsonObject
                ?.get("name")
                ?.jsonPrimitive
                ?.contentOrNull
        } else {
            null
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

    private fun updateFileBlock(
        blockId: String,
        uploadId: String,
        fileName: String,
    ) {
        val request = authorised(HttpPatch("$baseUrl/v1/blocks/$blockId"))
        request.addHeader("Content-Type", "application/json")
        request.entity =
            StringEntity(
                buildJsonObject {
                    put("type", FILE_TYPE)
                    put(
                        FILE_TYPE,
                        buildJsonObject {
                            put(
                                "file_upload",
                                buildJsonObject { put("id", uploadId) },
                            )
                            put("name", fileName)
                        },
                    )
                }.toString(),
                ContentType.APPLICATION_JSON,
            )

        execute(request)
    }

    private fun deleteBlock(blockId: String) {
        execute(authorised(HttpDelete("$baseUrl/v1/blocks/$blockId")))
    }

    private fun <T : HttpRequestBase> authorised(request: T): T {
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
        const val FILE_TYPE = "file"
        const val PAGE_SIZE = 100
        val SUCCESS_RANGE = 200..299
    }
}
