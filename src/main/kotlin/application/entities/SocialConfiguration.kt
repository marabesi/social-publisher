package application.entities

import kotlinx.serialization.Contextual

@kotlinx.serialization.Serializable
data class SocialConfiguration(
    val fileName: String = "",
    var storage: String = "",
    @Contextual
    var twitter: TwitterCredentials? = null
)

data class TwitterCredentials(
    val consumerKey: String = "",
    val consumerSecret: String = "",
    val accessToken: String = "",
    val accessTokenSecret: String = "",
)