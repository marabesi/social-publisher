package socialPosts

@kotlinx.serialization.Serializable
data class SocialConfiguration(
    val fileName: String = "",
    var storage: String = "",
)