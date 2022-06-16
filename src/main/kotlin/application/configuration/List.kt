package application.configuration

import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class List(
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
) {

    fun invoke(): String {
        return try {
            val data: SocialConfiguration = configurationRepository.find()
            cliOutput.write(Json.encodeToString(data))
        } catch (error: MissingConfiguration) {
            cliOutput.write(error.message!!)
        }
    }
}