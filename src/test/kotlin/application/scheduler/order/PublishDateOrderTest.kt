package application.scheduler.order

import application.entities.ScheduledItem
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class PublishDateOrderTest {
    private val post = SocialPosts(id = "1", text = "anything")

    @Test
    fun `should order scheduled items ascending by publish date`() {
        val later = ScheduledItem(post, Instant.parse("2023-11-02T10:00:00Z"), "1")
        val earlier = ScheduledItem(post, Instant.parse("2022-10-02T09:00:00Z"), "2")

        val result = PublishDateOrder(Direction.ASC).apply(arrayListOf(later, earlier))

        assertEquals(arrayListOf(earlier, later), result)
    }

    @Test
    fun `should order scheduled items descending by publish date`() {
        val earlier = ScheduledItem(post, Instant.parse("2022-10-02T09:00:00Z"), "1")
        val later = ScheduledItem(post, Instant.parse("2023-11-02T10:00:00Z"), "2")

        val result = PublishDateOrder(Direction.DESC).apply(arrayListOf(earlier, later))

        assertEquals(arrayListOf(later, earlier), result)
    }

    @Test
    fun `should keep an empty list empty`() {
        val result = PublishDateOrder(Direction.ASC).apply(arrayListOf())

        assertEquals(0, result.size)
    }
}
