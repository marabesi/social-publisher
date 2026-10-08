package adapters.inbound.rest.dto

data class SchedulePostRequest(
    val postId: String = "",
    val publishDate: String = "",
    val socialMedia: String = "TWITTER",
)
