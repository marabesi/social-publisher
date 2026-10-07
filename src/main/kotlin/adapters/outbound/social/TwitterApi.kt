package adapters.outbound.social

object TwitterApi {
    private const val DEFAULT_BASE_URL = "https://api.twitter.com"

    fun baseUrl(): String =
        System.getProperty("twitter.api.baseUrl")
            ?: System.getenv("TWITTER_API_BASE_URL")
            ?: DEFAULT_BASE_URL
}
