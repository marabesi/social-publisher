package thirdpartyintegration

import adapters.outbound.notion.HttpNotionFileUploader
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.test.assertContains

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

    @Test
    fun `logs the full response from the notion api`() {
        WireMockNotion.start()
        val file = file("posts-prod.csv")

        val uploader = HttpNotionFileUploader(apiKey = "test-key", pageId = "page-id")
        val logger = Logger.getLogger(HttpNotionFileUploader::class.java.name)
        val records = mutableListOf<LogRecord>()
        val handler =
            object : Handler() {
                override fun publish(record: LogRecord) {
                    records.add(record)
                }

                override fun flush() = Unit

                override fun close() = Unit
            }
        logger.addHandler(handler)

        try {
            uploader.upload(file)
        } finally {
            logger.removeHandler(handler)
        }

        val logged = records.filter { it.level == Level.INFO }.joinToString("\n") { it.message }
        assertContains(logged, """{"object":"file_upload","id":"test-upload-id","status":"pending"}""")
        assertContains(logged, """{"object":"file_upload","id":"test-upload-id","status":"uploaded"}""")
        assertContains(logged, """{"object":"list"}""")
    }

    private fun file(name: String): File = File(tempDir.toFile(), name).apply { writeText("hello,1") }
}
