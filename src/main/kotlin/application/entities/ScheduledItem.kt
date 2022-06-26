package application.entities

import java.time.Instant

data class ScheduledItem(
    val post: SocialPosts,
    val publishDate: Instant,
    val id: String? = "",
    var published: Boolean = false,
)