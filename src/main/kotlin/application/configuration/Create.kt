package application.configuration

import application.Messages
import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Create(
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
) {
    fun invoke(configuration: String): String {
        if (configuration.isEmpty()) {
            return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
        }
        val data = Json.decodeFromString<SocialConfiguration>(configuration)

        if (data.storage.isEmpty()) {
            data.storage = "csv"
        }

        configurationRepository.save(data)
        return cliOutput.write("Configuration has been stored")
    }
}
