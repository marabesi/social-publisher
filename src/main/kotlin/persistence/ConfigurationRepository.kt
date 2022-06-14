package persistence

import socialPosts.SocialConfiguration

interface ConfigurationRepository {
    fun save(socialConfiguration: SocialConfiguration): Boolean
    fun find(): SocialConfiguration
}
