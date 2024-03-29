package adapters.outbound.csv

import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Suppress("TooGenericExceptionCaught")
class FileSystemConfigurationRepository : ConfigurationRepository {
    private val configurationPath = "data"
    private val fileName = "global"

    override fun save(configuration: SocialConfiguration): SocialConfiguration {
        val fullPath = "$configurationPath/$fileName.json"
        val file = File(fullPath)

        ensureFileExists(file)

        val writer = FileWriter(file)
        writer.write(Json.encodeToString(configuration))
        writer.close()

        return configuration
    }

    override fun find(): SocialConfiguration {
        val fullPath = "$configurationPath/$fileName.json"

        if (!File(fullPath).exists()) {
            throw MissingConfiguration()
        }

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
