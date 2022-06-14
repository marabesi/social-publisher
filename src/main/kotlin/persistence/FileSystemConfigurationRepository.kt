package persistence;

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import socialPosts.SocialConfiguration
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileSystemConfigurationRepository: ConfigurationRepository {
    private val configurationFileName = "configuration.json"
    override fun save(path: String): SocialConfiguration {
        val fullPath = "$path/$configurationFileName"
        val file = File(fullPath)

        ensureFileExists(file)

        val writer = FileWriter(file)
        val socialConfiguration = SocialConfiguration(fullPath)
        writer.write(Json.encodeToString(socialConfiguration))
        writer.close()

        return socialConfiguration
    }

    override fun find(path: String): SocialConfiguration {
        val fullPath = "$path/$configurationFileName"

        val reader = FileReader(fullPath)
        val string = reader.readText()

        return Json.decodeFromString(string)
    }

    private fun ensureFileExists(file: File) {
        if (!file.isDirectory) {
            file.parentFile.mkdirs()
        }
    }
}
