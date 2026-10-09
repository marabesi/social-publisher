package application.scheduler

import MockedOutput
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class ScheduleSearchTest {
    private val schedules =
        listOf(
            ScheduledItem(SocialPosts(id = "1", text = "hello desktop"), Instant.EPOCH),
            ScheduledItem(SocialPosts(id = "2", text = "second post"), Instant.EPOCH),
            ScheduledItem(SocialPosts(id = "3", text = "release notes"), Instant.EPOCH),
        )

    @Test
    fun `keeps every schedule when no criteria is provided`() {
        assertEquals(schedules, ScheduleSearch().filter(schedules))
    }

    @Test
    fun `matches the post text by word prefix`() {
        val result = ScheduleSearch(text = "hello desk").filter(schedules)

        assertEquals(listOf("1"), result.map { it.post.id })
    }

    @Test
    fun `does not match terms hidden inside other words`() {
        val sample =
            listOf(
                ScheduledItem(SocialPosts(id = "1", text = "these are the laws"), Instant.EPOCH),
            )

        assertTrue(ScheduleSearch(text = "aws").filter(sample).isEmpty())
    }

    @Test
    fun `ignores text that is not present`() {
        assertTrue(ScheduleSearch(text = "zzz").filter(schedules).isEmpty())
    }

    @Test
    fun `filters by more than one post id`() {
        val result = ScheduleSearch(postIds = listOf("1", "3")).filter(schedules)

        assertEquals(listOf("1", "3"), result.map { it.post.id })
    }

    @Test
    fun `filters by social media`() {
        val twitter = ScheduledItem(schedules[0].post, Instant.EPOCH, "10", socialMedia = SocialMedia.TWITTER)
        val linkedin = ScheduledItem(schedules[1].post, Instant.EPOCH, "11", socialMedia = SocialMedia.LINKEDIN)

        val result = ScheduleSearch(socialMedia = SocialMedia.LINKEDIN).filter(listOf(twitter, linkedin))

        assertEquals(listOf("2"), result.map { it.post.id })
    }

    @Test
    fun `combines text id and social media filters`() {
        val first = ScheduledItem(schedules[0].post, Instant.EPOCH, "10", socialMedia = SocialMedia.TWITTER)
        val third = ScheduledItem(schedules[2].post, Instant.EPOCH, "11", socialMedia = SocialMedia.TWITTER)

        val result =
            ScheduleSearch
                .from(text = "notes", postIds = "1,3", socialMedia = SocialMedia.TWITTER)
                .filter(listOf(first, third))

        assertEquals(listOf("3"), result.map { it.post.id })
    }

    @Test
    fun `builds the criteria from raw values`() {
        val criteria = ScheduleSearch.from(text = "notes", postIds = "1, 3", socialMedia = SocialMedia.LINKEDIN)

        assertEquals("notes", criteria.text)
        assertEquals(listOf("1", "3"), criteria.postIds)
        assertEquals(SocialMedia.LINKEDIN, criteria.socialMedia)
    }

    @Test
    fun `list applies the search before formatting the output`() {
        val schedulerRepository = InMemorySchedulerRepository()
        val first = ScheduledItem(SocialPosts(id = "1", text = "hello desktop"), Instant.parse("2022-10-02T09:00:00Z"))
        schedulerRepository.save(first)

        val output =
            List(
                schedulerRepository,
                MockedOutput(),
                arrayListOf(),
                "",
                search = ScheduleSearch(text = "zzz"),
            ).invoke()

        assertEquals("No posts scheduled", output)
    }
}
