package application.socialnetwork

data class LinkedInToken(
    val accessToken: String,
    val authorUrn: String,
)

fun interface ExchangeLinkedInAuthorization {
    fun exchange(code: String): LinkedInToken
}
