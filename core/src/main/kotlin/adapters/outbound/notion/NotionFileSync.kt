package adapters.outbound.notion

import adapters.outbound.csv.StorePath
import application.entities.NotionCredentials
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import java.io.File

class NotionFileSync(
    private val configurationRepository: ConfigurationRepository,
    private val storePath: StorePath = StorePath.DEFAULT,
    private val uploaderFactory: (NotionCredentials) -> NotionFileUploader = {
        HttpNotionFileUploader(it.apiKey, it.pageId)
    },
) : NotionSync {
    override fun sync() {
        val configuration = runCatching { configurationRepository.find() }.getOrNull() ?: return
        if (!configuration.storage.equals(STORAGE, ignoreCase = true)) {
            return
        }

        val credentials = configuration.notion ?: return
        if (credentials.apiKey.isBlank()) {
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
        const val STORAGE = "notion"
        const val DEFAULT_FILE_NAME = "production"
    }
}
