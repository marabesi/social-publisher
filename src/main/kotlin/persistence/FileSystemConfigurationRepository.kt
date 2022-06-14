package persistence;

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import socialPosts.SocialConfiguration
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileSystemConfigurationRepository: ConfigurationRepository {
    override fun save(path: String): SocialConfiguration {
        val fullPath = "$path/configuration.json"
        val file = File(fullPath)

        ensureFileExists(file)

        val writer = FileWriter(file)
        val socialConfiguration = SocialConfiguration(fullPath)
        writer.write(Json.encodeToString(socialConfiguration))
        writer.close()

        return socialConfiguration
    }

    private fun ensureFileExists(file: File) {
        if (!file.isDirectory) {
            file.parentFile.mkdirs()
        }
    }

    override fun find(path: String): SocialConfiguration {
        val file = File("$path/configuration.json")

        val reader = FileReader(file)
        val string = reader.readText()

        return Json.decodeFromString(string)
    }
}
