package adapters.outbound.social

object LinkedinApi {
    private const val DEFAULT_BASE_URL = "https://api.linkedin.com"
    private const val DEFAULT_OAUTH_BASE_URL = "https://www.linkedin.com"

    fun baseUrl(): String =
        System.getProperty("linkedin.api.baseUrl")
            ?: System.getenv("LINKEDIN_API_BASE_URL")
            ?: DEFAULT_BASE_URL

    fun oauthBaseUrl(): String =
        System.getProperty("linkedin.oauth.baseUrl")
            ?: System.getenv("LINKEDIN_OAUTH_BASE_URL")
            ?: DEFAULT_OAUTH_BASE_URL
}
