package adapters.outbound.googledrive

import adapters.outbound.csv.StorePath
import application.entities.GoogleDriveCredentials
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import java.io.File

class GoogleDriveFileSync(
    private val configurationRepository: ConfigurationRepository,
    private val storePath: StorePath = StorePath.DEFAULT,
    private val uploaderFactory: (GoogleDriveCredentials) -> GoogleDriveFileUploader = {
        HttpGoogleDriveFileUploader(it.accessToken, it.folderId)
    },
) : GoogleDriveSync {
    override fun sync() {
        val configuration = runCatching { configurationRepository.find() }.getOrNull() ?: return
        if (!configuration.storage.equals(STORAGE, ignoreCase = true)) {
            return
        }

        val credentials = configuration.googleDrive ?: return
        if (credentials.accessToken.isBlank()) {
            return
        }

        val uploader = uploaderFactory(credentials)
        dataFiles(configuration).filter { it.exists() }.forEach(uploader::upload)
    }

    private fun dataFiles(configuration: SocialConfiguration): List<File> {
        val suffix = configuration.fileName.ifBlank { DEFAULT_FILE_NAME }
        return listOf(
            File(storePath.file("posts-$suffix.csv")),
            File(storePath.file("scheduler-$suffix.csv")),
        )
    }

    companion object {
        const val STORAGE = "google_drive"
        const val DEFAULT_FILE_NAME = "production"
    }
}
