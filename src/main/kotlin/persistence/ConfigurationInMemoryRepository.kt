package persistence

import socialPosts.SocialConfiguration

class ConfigurationInMemoryRepository: ConfigurationRepository {
    private val configurationList: ArrayList<SocialConfiguration> = arrayListOf()

    override fun save(fileName: String): SocialConfiguration {
        val fullPath = "data/$fileName.json"
        configurationList.add(SocialConfiguration(fullPath))
        return configurationList[0]
    }

    override fun find(fileName: String): SocialConfiguration {
        return configurationList[0]
    }
}
