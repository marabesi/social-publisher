package adapters.outbound.bluesky

object BlueskyApi {
    private const val DEFAULT_BASE_URL = "https://bsky.social"

    fun baseUrl(): String =
        System.getProperty("bluesky.api.baseUrl")
            ?: System.getenv("BLUESKY_API_BASE_URL")
            ?: DEFAULT_BASE_URL
}
