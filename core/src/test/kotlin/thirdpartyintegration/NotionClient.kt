package thirdpartyintegration

import adapters.outbound.notion.HttpNotionFileUploader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class NotionClient {
    @BeforeEach
    fun setUp() {
        WireMockNotion.start()
    }

    @AfterEach
    fun tearDown() {
        WireMockNotion.stop()
    }

    @Test
    fun `uploads a file and returns the file upload id`() {
        val file = File.createTempFile("posts-", ".csv").apply { writeText("hello,1") }

        val uploader = HttpNotionFileUploader(apiKey = "test-key", pageId = "page-id")

        val uploadId = uploader.upload(file)

        assertEquals("test-upload-id", uploadId)
    }
}
