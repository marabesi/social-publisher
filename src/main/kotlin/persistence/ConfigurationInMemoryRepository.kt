package persistence

import socialPosts.SocialConfiguration

class ConfigurationInMemoryRepository: ConfigurationRepository {
    private val configurationList: ArrayList<SocialConfiguration> = arrayListOf()

    override fun save(socialConfiguration: SocialConfiguration): Boolean {
        configurationList.add(socialConfiguration)
        return true;
    }

    override fun find(): SocialConfiguration {
        return configurationList[0]
    }
}
