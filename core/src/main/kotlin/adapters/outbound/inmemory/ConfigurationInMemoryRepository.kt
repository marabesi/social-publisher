package adapters.outbound.inmemory

import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration

class ConfigurationInMemoryRepository : ConfigurationRepository {
    private var stored: SocialConfiguration? = null

    override fun save(configuration: SocialConfiguration): SocialConfiguration {
        stored = configuration
        return configuration
    }

    override fun find(): SocialConfiguration = stored ?: throw MissingConfiguration()
}
