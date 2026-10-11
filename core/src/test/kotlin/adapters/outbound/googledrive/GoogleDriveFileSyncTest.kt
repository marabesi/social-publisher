package adapters.outbound.googledrive

import adapters.outbound.csv.StorePath
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import application.entities.GoogleDriveCredentials
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class GoogleDriveFileSyncTest {
    @TempDir
    lateinit var tempDir: Path

    private class RecordingUploader : GoogleDriveFileUploader {
        val uploadedFiles = mutableListOf<String>()

        override fun upload(file: File): String {
            uploadedFiles.add(file.name)
            return "file-id"
        }
    }

    private val storePath = StorePath { tempDir.toString() }

    @Test
    fun `uploads the data files when google drive storage is configured`() {
        val configurationRepository = configuredRepository(storage = "google_drive")

        File(tempDir.toFile(), "posts-prod.csv").writeText("hello,1")
        File(tempDir.toFile(), "scheduler-prod.csv").writeText("1,2014-12-22T10:15:30Z,1,false")

        val uploader = RecordingUploader()
        val sync = GoogleDriveFileSync(configurationRepository, storePath) { uploader }

        sync.sync()

        assertEquals(listOf("posts-prod.csv", "scheduler-prod.csv"), uploader.uploadedFiles)
    }

    @Test
    fun `uses the configured file name as the data file suffix`() {
        val configurationRepository = configuredRepository(storage = "google_drive", fileName = "e2e-file")

        File(tempDir.toFile(), "posts-e2e-file.csv").writeText("hello,1")
        File(tempDir.toFile(), "scheduler-e2e-file.csv").writeText("1,2014-12-22T10:15:30Z,1,false")

        val uploader = RecordingUploader()
        val sync = GoogleDriveFileSync(configurationRepository, storePath) { uploader }

        sync.sync()

        assertEquals(listOf("posts-e2e-file.csv", "scheduler-e2e-file.csv"), uploader.uploadedFiles)
    }

    @Test
    fun `does not upload when the storage is csv`() {
        val configurationRepository = configuredRepository(storage = "csv")

        File(tempDir.toFile(), "posts-prod.csv").writeText("hello,1")

        val uploader = RecordingUploader()
        val sync = GoogleDriveFileSync(configurationRepository, storePath) { uploader }

        sync.sync()

        assertEquals(0, uploader.uploadedFiles.size)
    }

    @Test
    fun `does not upload without google drive credentials`() {
        val configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(SocialConfiguration(storage = "google_drive"))

        File(tempDir.toFile(), "posts-prod.csv").writeText("hello,1")

        val uploader = RecordingUploader()
        val sync = GoogleDriveFileSync(configurationRepository, storePath) { uploader }

        sync.sync()

        assertEquals(0, uploader.uploadedFiles.size)
    }

    @Test
    fun `does not throw when no configuration is stored`() {
        val configurationRepository = ConfigurationInMemoryRepository()
        val sync = GoogleDriveFileSync(configurationRepository, storePath) { RecordingUploader() }

        assertDoesNotThrow { sync.sync() }
    }

    private fun configuredRepository(
        storage: String,
        fileName: String = "prod",
    ): ConfigurationInMemoryRepository {
        val configurationRepository = ConfigurationInMemoryRepository()
        configurationRepository.save(
            SocialConfiguration(
                fileName = fileName,
                storage = storage,
                googleDrive = GoogleDriveCredentials(accessToken = "secret", folderId = "folder-id"),
            ),
        )
        return configurationRepository
    }
}
