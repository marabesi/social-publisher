package persistence

import socialPosts.SocialConfiguration

interface ConfigurationRepository {
    fun save(configuration: SocialConfiguration): SocialConfiguration
    fun find(): SocialConfiguration
}
