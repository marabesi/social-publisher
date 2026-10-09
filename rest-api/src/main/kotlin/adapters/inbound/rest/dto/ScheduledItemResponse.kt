package adapters.inbound.rest.dto

import application.entities.SocialMedia

data class ScheduledItemResponse(
    val id: String?,
    val postId: String?,
    val text: String,
    val publishDate: String,
    val published: Boolean,
    val socialMedia: SocialMedia,
)
