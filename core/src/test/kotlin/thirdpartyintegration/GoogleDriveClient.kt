package thirdpartyintegration

import adapters.outbound.googledrive.HttpGoogleDriveFileUploader
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

class GoogleDriveClient {
    @TempDir
    lateinit var tempDir: Path

    @AfterEach
    fun tearDown() {
        WireMockGoogleDrive.stop()
    }

    @Test
    fun `uploads a file and returns the file id`() {
        WireMockGoogleDrive.start()
        val file = file("posts-prod.csv")

        val uploader = HttpGoogleDriveFileUploader(accessToken = "test-token", folderId = "folder-id")

        val fileId = uploader.upload(file)

        assertEquals("test-file-id", fileId)
        assertEquals(1, WireMockGoogleDrive.listCalls())
        assertEquals(1, WireMockGoogleDrive.createCalls())
        assertEquals(0, WireMockGoogleDrive.updateCalls())
    }

    @Test
    fun `replaces the existing file instead of creating a duplicate`() {
        WireMockGoogleDrive.start(existingFileNames = listOf("posts-prod.csv"))
        val file = file("posts-prod.csv")

        val uploader = HttpGoogleDriveFileUploader(accessToken = "test-token", folderId = "folder-id")

        val fileId = uploader.upload(file)

        assertEquals("existing-file-id-0", fileId)
        assertEquals(0, WireMockGoogleDrive.createCalls())
        assertEquals(1, WireMockGoogleDrive.updateCalls())
    }

    @Test
    fun `logs the full response from the google drive api`() {
        WireMockGoogleDrive.start()
        val file = file("posts-prod.csv")

        val uploader = HttpGoogleDriveFileUploader(accessToken = "test-token", folderId = "folder-id")
        val logger = Logger.getLogger(HttpGoogleDriveFileUploader::class.java.name)
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
        assertContains(logged, """{"files":[]}""")
        assertContains(logged, """{"kind":"drive#file","id":"test-file-id","name":"posts-prod.csv"}""")
    }

    private fun file(name: String): File = File(tempDir.toFile(), name).apply { writeText("hello,1") }
}
