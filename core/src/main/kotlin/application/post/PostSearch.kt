package application.post

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.search.IdList
import application.search.TextSearch

data class PostSearch(
    val text: String = "",
    val ids: kotlin.collections.List<String> = emptyList(),
    val socialMedia: SocialMedia? = null,
) {
    fun filter(
        posts: kotlin.collections.List<SocialPosts>,
        schedules: kotlin.collections.List<ScheduledItem> = emptyList(),
    ): kotlin.collections.List<SocialPosts> {
        val scheduledIds = scheduledPostIds(schedules)

        return posts.filter { post ->
            val id = post.id.orEmpty()
            TextSearch.matches(text, post.text) &&
                (ids.isEmpty() || id in ids) &&
                (socialMedia == null || id in scheduledIds)
        }
    }

    private fun scheduledPostIds(schedules: kotlin.collections.List<ScheduledItem>): Set<String> =
        if (socialMedia == null) {
            emptySet()
        } else {
            schedules
                .filter { it.socialMedia == socialMedia }
                .mapNotNull { it.post.id }
                .toSet()
        }

    companion object {
        fun from(
            text: String = "",
            ids: String = "",
            socialMedia: SocialMedia? = null,
        ): PostSearch = PostSearch(text, IdList.parse(ids), socialMedia)
    }
}
