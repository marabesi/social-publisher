package persistence

import cli.MissingConfiguration
import socialPosts.SocialConfiguration

class ConfigurationInMemoryRepository: ConfigurationRepository {
    private val configurationList: ArrayList<SocialConfiguration> = arrayListOf()

    override fun save(configuration: SocialConfiguration): SocialConfiguration {
        configurationList.add(configuration)
        return configurationList[0]
    }

    override fun find(): SocialConfiguration {
        if (configurationList.isEmpty()) {
            throw MissingConfiguration()
        }
        return configurationList[0]
    }
}
