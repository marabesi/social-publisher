package adapters.outbound.notion

object NotionApi {
    private const val DEFAULT_BASE_URL = "https://api.notion.com"

    const val VERSION = "2026-03-11"
    const val VERSION_HEADER = "Notion-Version"

    fun baseUrl(): String =
        System.getProperty("notion.api.baseUrl")
            ?: System.getenv("NOTION_API_BASE_URL")
            ?: DEFAULT_BASE_URL
}
