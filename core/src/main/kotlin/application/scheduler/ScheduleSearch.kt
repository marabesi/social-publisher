package application.scheduler

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.search.IdList
import application.search.TextSearch

data class ScheduleSearch(
    val text: String = "",
    val postIds: kotlin.collections.List<String> = emptyList(),
    val socialMedia: SocialMedia? = null,
) {
    fun filter(schedules: kotlin.collections.List<ScheduledItem>): kotlin.collections.List<ScheduledItem> =
        schedules.filter { schedule ->
            TextSearch.matches(text, schedule.post.text) &&
                (postIds.isEmpty() || schedule.post.id.orEmpty() in postIds) &&
                (socialMedia == null || schedule.socialMedia == socialMedia)
        }

    companion object {
        fun from(
            text: String = "",
            postIds: String = "",
            socialMedia: SocialMedia? = null,
        ): ScheduleSearch = ScheduleSearch(text, IdList.parse(postIds), socialMedia)
    }
}
