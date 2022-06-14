package persistence

import socialPosts.SocialConfiguration

interface ConfigurationRepository {
    fun save(fileName: String): SocialConfiguration
    fun find(fileName: String): SocialConfiguration
}
