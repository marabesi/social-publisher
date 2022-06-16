package application.persistence

import application.socialPosts.SocialConfiguration

interface ConfigurationRepository {
    fun save(configuration: SocialConfiguration): SocialConfiguration
    fun find(): SocialConfiguration
}
