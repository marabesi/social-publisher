package socialPosts

import java.time.Instant

data class ScheduledItem(
    val post: SocialPosts,
    val publishDate: Instant
)