package adapters.inbound.rest.dto

data class ScheduledItemResponse(
    val id: String?,
    val postId: String?,
    val text: String,
    val publishDate: String,
    val published: Boolean,
)
