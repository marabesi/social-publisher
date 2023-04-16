package application.configuration

import application.Messages
import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

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
                data.storage = "csv"
            }

            if (data.timezone.isEmpty()) {
                data.timezone = "UTC"
            }

            configurationRepository.save(data)
        } catch (error: ConfigurationGivenHasInvalidProperty) {
            return cliOutput.write(error.message.toString())
        }

        return cliOutput.write("Configuration has been stored")
    }

    private val json = Json { ignoreUnknownKeys = true }

    private fun validateConfigurationJson(configuration: String) {
        val keys = json.parseToJsonElement(configuration)
        val given = keys.jsonObject.keys
        val available = listOf("fileName", "storage", "twitter", "timezone")

        given.forEach {
            if (!available.contains(it)) {
                throw ConfigurationGivenHasInvalidProperty(it)
            }
        }
    }
}
