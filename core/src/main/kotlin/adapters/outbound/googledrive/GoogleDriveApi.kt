package adapters.outbound.googledrive

object GoogleDriveApi {
    private const val DEFAULT_BASE_URL = "https://www.googleapis.com"

    fun baseUrl(): String =
        System.getProperty("google.drive.api.baseUrl")
            ?: System.getenv("GOOGLE_DRIVE_API_BASE_URL")
            ?: DEFAULT_BASE_URL
}
