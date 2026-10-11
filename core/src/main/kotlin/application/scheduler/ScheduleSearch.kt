package application.scheduler

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.search.IdList
import application.search.TextSearch

data class ScheduleSearch(
    val text: String = "",
    val postIds: kotlin.collections.List<String> = emptyList(),
    val socialMedia: SocialMedia? = null,
    val status: ScheduleStatus = ScheduleStatus.UPCOMING,
) {
    fun filter(schedules: kotlin.collections.List<ScheduledItem>): kotlin.collections.List<ScheduledItem> =
        schedules.filter { schedule ->
            TextSearch.matches(text, schedule.post.text) &&
                (postIds.isEmpty() || schedule.post.id.orEmpty() in postIds) &&
                (socialMedia == null || schedule.socialMedia == socialMedia) &&
                schedule.published == status.published
        }

    companion object {
        fun from(
            text: String = "",
            postIds: String = "",
            socialMedia: SocialMedia? = null,
            status: ScheduleStatus = ScheduleStatus.UPCOMING,
        ): ScheduleSearch = ScheduleSearch(text, IdList.parse(postIds), socialMedia, status)
    }
}
