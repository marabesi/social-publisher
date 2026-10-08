package application.scheduler.filters

import application.entities.ScheduledItem
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class PropertyFilterTest {
    private val item =
        ScheduledItem(
            SocialPosts(id = "1", text = "release notes", socialMediaId = "42"),
            Instant.parse("2022-07-10T09:00:00Z"),
            "3",
            false,
        )

    @Test
    fun `should match a top level property`() {
        assertTrue(PropertyFilter("published", "false").applyPredicateFor(item))
        assertFalse(PropertyFilter("published", "true").applyPredicateFor(item))
    }

    @Test
    fun `should match a nested post property`() {
        assertTrue(PropertyFilter("post.text", "release notes").applyPredicateFor(item))
        assertFalse(PropertyFilter("post.text", "draft").applyPredicateFor(item))
    }

    @Test
    fun `should match the publish date parts`() {
        assertTrue(PropertyFilter("year", "2022").applyPredicateFor(item))
        assertTrue(PropertyFilter("month", "07").applyPredicateFor(item))
        assertTrue(PropertyFilter("day", "10").applyPredicateFor(item))
    }

    @Test
    fun `should match the full publish date`() {
        assertTrue(PropertyFilter("publishDate", "2022-07-10T09:00:00Z").applyPredicateFor(item))
    }

    @Test
    fun `should not match an unknown property`() {
        assertFalse(PropertyFilter("description", "anything").applyPredicateFor(item))
    }
}
