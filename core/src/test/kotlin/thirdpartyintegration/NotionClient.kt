package thirdpartyintegration

import adapters.outbound.notion.HttpNotionFileUploader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class NotionClient {
    @TempDir
    lateinit var tempDir: Path

    @AfterEach
    fun tearDown() {
        WireMockNotion.stop()
    }

    @Test
    fun `uploads a file and returns the file upload id`() {
        WireMockNotion.start()
        val file = file("posts-prod.csv")

        val uploader = HttpNotionFileUploader(apiKey = "test-key", pageId = "page-id")

        val uploadId = uploader.upload(file)

        assertEquals("test-upload-id", uploadId)
        assertEquals(0, WireMockNotion.updateBlockCalls())
        assertEquals(1, WireMockNotion.appendBlockCalls())
    }

    @Test
    fun `replaces the existing file block instead of appending a duplicate`() {
        WireMockNotion.start(existingFileNames = listOf("posts-prod.csv"))
        val file = file("posts-prod.csv")

        val uploader = HttpNotionFileUploader(apiKey = "test-key", pageId = "page-id")

        val uploadId = uploader.upload(file)

        assertEquals("test-upload-id", uploadId)
        assertEquals(1, WireMockNotion.updateBlockCalls())
        assertEquals(0, WireMockNotion.appendBlockCalls())
        assertEquals(0, WireMockNotion.deleteBlockCalls())
    }

    @Test
    fun `keeps a single copy when the file block was already duplicated`() {
        WireMockNotion.start(existingFileNames = listOf("posts-prod.csv", "posts-prod.csv"))
        val file = file("posts-prod.csv")

        val uploader = HttpNotionFileUploader(apiKey = "test-key", pageId = "page-id")

        uploader.upload(file)

        assertEquals(1, WireMockNotion.updateBlockCalls())
        assertEquals(0, WireMockNotion.appendBlockCalls())
        assertEquals(1, WireMockNotion.deleteBlockCalls())
    }

    private fun file(name: String): File = File(tempDir.toFile(), name).apply { writeText("hello,1") }
}
