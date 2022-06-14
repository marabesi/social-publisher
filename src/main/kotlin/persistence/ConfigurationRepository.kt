package persistence

import socialPosts.SocialConfiguration

interface ConfigurationRepository {
    fun save(path: String): SocialConfiguration
    fun find(path: String): SocialConfiguration
}
