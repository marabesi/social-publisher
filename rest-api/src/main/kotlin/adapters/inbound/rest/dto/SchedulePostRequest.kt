package adapters.inbound.rest.dto

import application.entities.SocialMedia

data class SchedulePostRequest(
    val postId: String = "",
    val publishDate: String = "",
    val socialMedia: SocialMedia = SocialMedia.TWITTER,
)
