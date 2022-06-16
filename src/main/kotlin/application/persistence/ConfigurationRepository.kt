package application.persistence

import application.entities.SocialConfiguration

interface ConfigurationRepository {
    fun save(configuration: SocialConfiguration): SocialConfiguration
    fun find(): SocialConfiguration
}
