package adapters.outbound.googledrive

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPatch
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.HttpClients
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.logging.Logger

class HttpGoogleDriveFileUploader(
    private val accessToken: String,
    private val folderId: String = "",
    private val baseUrl: String = GoogleDriveApi.baseUrl(),
) : GoogleDriveFileUploader {
    private val httpClient = HttpClients.createDefault()
    private val logger = Logger.getLogger(HttpGoogleDriveFileUploader::class.java.name)

    override fun upload(file: File): String {
        val existingId = findFileId(file.name)
        return if (existingId == null) {
            create(file)
        } else {
            update(existingId, file)
        }
    }

    private fun create(file: File): String {
        val boundary = "social-publisher-${UUID.randomUUID()}"
        val request = authorised(HttpPost("$baseUrl/upload/drive/v3/files?uploadType=multipart"))
        request.entity =
            ByteArrayEntity(
                multipartBody(boundary, metadata(file), file),
                ContentType.parse("$MULTIPART_RELATED; boundary=$boundary"),
            )

        val body = execute(request)
        return Json
            .parseToJsonElement(body)
            .jsonObject
            .getValue("id")
            .jsonPrimitive
            .content
    }

    private fun update(
        fileId: String,
        file: File,
    ): String {
        val request = authorised(HttpPatch("$baseUrl/upload/drive/v3/files/$fileId?uploadType=media"))
        request.entity = ByteArrayEntity(file.readBytes(), ContentType.create(CSV_CONTENT_TYPE))

        execute(request)
        return fileId
    }

    private fun findFileId(fileName: String): String? {
        val query =
            buildString {
                append("name='").append(fileName.replace("'", "\\'")).append("'")
                if (folderId.isNotBlank()) {
                    append(" and '").append(folderId).append("' in parents")
                }
                append(" and trashed=false")
            }
        val url =
            "$baseUrl/drive/v3/files?q=${URLEncoder.encode(query, StandardCharsets.UTF_8)}" +
                "&fields=files(id,name)"

        return Json
            .parseToJsonElement(execute(authorised(HttpGet(url))))
            .jsonObject["files"]
            ?.jsonArray
            ?.firstOrNull()
            ?.jsonObject
            ?.get("id")
            ?.jsonPrimitive
            ?.contentOrNull
    }

    private fun metadata(file: File): String =
        buildJsonObject {
            put("name", file.name)
            put("mimeType", CSV_CONTENT_TYPE)
            if (folderId.isNotBlank()) {
                put(
                    "parents",
                    buildJsonArray { add(JsonPrimitive(folderId)) },
                )
            }
        }.toString()

    private fun multipartBody(
        boundary: String,
        metadata: String,
        file: File,
    ): ByteArray {
        val header =
            buildString {
                append("--").append(boundary).append("\r\n")
                append("Content-Type: application/json; charset=UTF-8\r\n\r\n")
                append(metadata)
                append("\r\n--").append(boundary).append("\r\n")
                append("Content-Type: ").append(CSV_CONTENT_TYPE).append("\r\n\r\n")
            }.toByteArray(StandardCharsets.UTF_8)
        val footer = "\r\n--$boundary--\r\n".toByteArray(StandardCharsets.UTF_8)

        return header + file.readBytes() + footer
    }

    private fun <T : HttpRequestBase> authorised(request: T): T {
        request.addHeader("Authorization", "Bearer $accessToken")
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

            logger.info("Google Drive API response (${request.method} ${request.uri.path}): $body")

            val status = response.statusLine.statusCode
            if (status !in SUCCESS_RANGE) {
                throw CouldNotUploadToGoogleDriveException("Google Drive API returned $status: $body")
            }

            body
        }

    private companion object {
        const val CSV_CONTENT_TYPE = "text/csv"
        const val MULTIPART_RELATED = "multipart/related"
        val SUCCESS_RANGE = 200..299
    }
}
