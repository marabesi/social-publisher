package application.socialnetwork.linkedin

data class LinkedInToken(
    val accessToken: String,
    val authorUrn: String,
)

fun interface ExchangeLinkedInAuthorization {
    fun exchange(code: String): LinkedInToken
}
