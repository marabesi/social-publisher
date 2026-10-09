package application.entities

@kotlinx.serialization.Serializable
data class SocialConfiguration(
    val fileName: String = "",
    var storage: String = "",
    var twitter: TwitterCredentials? = null,
    var linkedin: LinkedInCredentials? = null,
    var timezone: String = "",
)

@kotlinx.serialization.Serializable
data class TwitterCredentials(
    val consumerKey: String = "",
    val consumerSecret: String = "",
    val accessToken: String = "",
    val accessTokenSecret: String = "",
)

@kotlinx.serialization.Serializable
data class LinkedInCredentials(
    val clientId: String = "",
    val clientSecret: String = "",
    val redirectUri: String = "",
    val accessToken: String = "",
    val authorUrn: String = "",
)
