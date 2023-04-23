package application.configuration

import application.Messages
import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

private const val DEFAULT_STORAGE_FORMAT = "csv"
private const val DEFAULT_TIMEZONE = "UTC"
private val AVAILABLE_CONFIGURATION = listOf("fileName", "storage", "twitter", "timezone")

class Create(
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
) {
    fun invoke(configuration: String): String {
        if (configuration.isEmpty()) {
            return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
        }

        try {
            validateConfigurationJson(configuration)

            val data = Json.decodeFromString<SocialConfiguration>(configuration)

            if (data.storage.isEmpty()) {
                data.storage = DEFAULT_STORAGE_FORMAT
            }

            if (data.timezone.isEmpty()) {
                data.timezone = DEFAULT_TIMEZONE
            }

            configurationRepository.save(data)
        } catch (error: ConfigurationGivenHasInvalidProperty) {
            return cliOutput.write(error.message.toString())
        }

        return cliOutput.write("Configuration has been stored")
    }

    private fun validateConfigurationJson(configuration: String) {
        val json = Json { ignoreUnknownKeys = true }
        val keys = json.parseToJsonElement(configuration)
        val given = keys.jsonObject.keys

        given.forEach {
            if (!AVAILABLE_CONFIGURATION.contains(it)) {
                throw ConfigurationGivenHasInvalidProperty(it)
            }
        }
    }
}
