package adapters.inbound.rest.dto

import application.entities.SocialMedia

data class RandomScheduleRequest(
    val publishDate: String = "",
    val socialMedia: SocialMedia = SocialMedia.TWITTER,
)
