package persistence

import socialPosts.SocialConfiguration

class ConfigurationInMemoryRepository: ConfigurationRepository {
    private val configurationList: ArrayList<SocialConfiguration> = arrayListOf()

    override fun save(configuration: SocialConfiguration): SocialConfiguration {
        configurationList.add(configuration)
        return configurationList[0]
    }

    override fun find(): SocialConfiguration {
        return configurationList[0]
    }
}
