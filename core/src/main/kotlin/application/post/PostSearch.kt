package application.post

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts

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
            matchesText(text, post.text) &&
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
        ): PostSearch = PostSearch(text, parsePostIds(ids), socialMedia)
    }
}

internal fun parsePostIds(raw: String): kotlin.collections.List<String> =
    raw
        .split(',', ';', ' ', '\n', '\t')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

private fun matchesText(
    query: String,
    text: String,
): Boolean {
    val tokens = query.lowercase().split(' ', '\n', '\t').filter { it.isNotEmpty() }
    if (tokens.isEmpty()) {
        return true
    }

    val haystack = text.lowercase()
    return tokens.all { isSubsequence(it, haystack) }
}

private fun isSubsequence(
    needle: String,
    haystack: String,
): Boolean {
    var index = 0
    for (character in haystack) {
        if (index < needle.length && needle[index] == character) {
            index++
        }
    }
    return index == needle.length
}
