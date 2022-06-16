package application.persistence.configuration

import application.entities.SocialConfiguration

interface ConfigurationRepository {
    fun save(configuration: SocialConfiguration): SocialConfiguration

    @kotlin.jvm.Throws(MissingConfiguration::class)
    fun find(): SocialConfiguration
}
