package persistence

import socialPosts.SocialConfiguration

class ConfigurationInMemoryRepository: ConfigurationRepository {
    private val configurationList: ArrayList<SocialConfiguration> = arrayListOf()

    override fun save(path: String): SocialConfiguration {
        val fullPath = "$path/configuration.json"
        configurationList.add(SocialConfiguration(fullPath))
        return configurationList[0]
    }

    override fun find(path: String): SocialConfiguration {
        return configurationList[0]
    }
}
