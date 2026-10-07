package application.scheduler.filters

import application.entities.ScheduledItem
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class ScheduledItemPropertiesTest {
    @Test
    fun `should expose the available keys for filtering`() {
        val keys = ScheduledItemProperties.availableKeys()

        assertTrue(
            keys.containsAll(
                listOf(
                    "id",
                    "published",
                    "publishDate",
                    "post.id",
                    "post.text",
                    "year",
                    "month",
                    "day",
                ),
            ),
        )
    }

    @Test
    fun `should expose values for the scheduled item`() {
        val item =
            ScheduledItem(
                SocialPosts(id = "1", text = "release notes"),
                Instant.parse("2022-07-10T09:00:00Z"),
                "3",
                true,
            )

        val properties = ScheduledItemProperties(item)

        assertEquals("true", properties.valueOf("published"))
        assertEquals("release notes", properties.valueOf("post.text"))
        assertEquals("2022", properties.valueOf("year"))
        assertEquals("7", properties.valueOf("month"))
        assertEquals("10", properties.valueOf("day"))
    }
}
