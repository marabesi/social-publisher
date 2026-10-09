package application.post

import MockedOutput
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class PostSearchTest {
    private val posts =
        listOf(
            SocialPosts(id = "1", text = "hello desktop"),
            SocialPosts(id = "2", text = "second post"),
            SocialPosts(id = "3", text = "release notes"),
        )

    @Test
    fun `keeps every post when no criteria is provided`() {
        assertEquals(posts, PostSearch().filter(posts))
    }

    @Test
    fun `matches text case insensitively`() {
        val result = PostSearch(text = "DESKTOP").filter(posts)

        assertEquals(listOf("1"), result.map { it.id })
    }

    @Test
    fun `matches the start of a word`() {
        val result = PostSearch(text = "hello desk").filter(posts)

        assertEquals(listOf("1"), result.map { it.id })
    }

    @Test
    fun `matches a term anywhere it starts a word`() {
        val sample = listOf(SocialPosts(id = "1", text = "deploying to AWS"))

        assertEquals(listOf("1"), PostSearch(text = "aws").filter(sample).map { it.id })
    }

    @Test
    fun `does not match a term hidden inside another word`() {
        val sample = listOf(SocialPosts(id = "1", text = "these are the laws"))

        assertTrue(PostSearch(text = "aws").filter(sample).isEmpty())
    }

    @Test
    fun `matches every token of a multi word query`() {
        val result = PostSearch(text = "release notes").filter(posts)

        assertEquals(listOf("3"), result.map { it.id })
    }

    @Test
    fun `ignores text that is not present`() {
        assertTrue(PostSearch(text = "zzz").filter(posts).isEmpty())
    }

    @Test
    fun `filters by a single post id`() {
        val result = PostSearch(ids = listOf("2")).filter(posts)

        assertEquals(listOf("2"), result.map { it.id })
    }

    @Test
    fun `filters by more than one post id`() {
        val result = PostSearch(ids = listOf("1", "3")).filter(posts)

        assertEquals(listOf("1", "3"), result.map { it.id })
    }

    @Test
    fun `parses post ids separated by commas spaces or semicolons`() {
        assertEquals(listOf("1", "2", "3"), PostSearch.from(ids = "1, 2;3\n").ids)
        assertTrue(PostSearch.from(ids = " , ; ").ids.isEmpty())
    }

    @Test
    fun `builds the criteria from raw values`() {
        val criteria = PostSearch.from(text = "notes", ids = "1,3", socialMedia = SocialMedia.TWITTER)

        assertEquals("notes", criteria.text)
        assertEquals(listOf("1", "3"), criteria.ids)
        assertEquals(SocialMedia.TWITTER, criteria.socialMedia)
    }

    @Test
    fun `filters by social network using the schedules`() {
        val schedules =
            listOf(
                ScheduledItem(posts[0], Instant.EPOCH, "10", socialMedia = SocialMedia.TWITTER),
                ScheduledItem(posts[1], Instant.EPOCH, "11", socialMedia = SocialMedia.LINKEDIN),
            )

        val twitter = PostSearch(socialMedia = SocialMedia.TWITTER).filter(posts, schedules)
        val linkedin = PostSearch(socialMedia = SocialMedia.LINKEDIN).filter(posts, schedules)

        assertEquals(listOf("1"), twitter.map { it.id })
        assertEquals(listOf("2"), linkedin.map { it.id })
    }

    @Test
    fun `combines text id and social network filters`() {
        val schedules =
            listOf(
                ScheduledItem(posts[0], Instant.EPOCH, "10", socialMedia = SocialMedia.TWITTER),
                ScheduledItem(posts[2], Instant.EPOCH, "11", socialMedia = SocialMedia.TWITTER),
            )

        val criteria = PostSearch.from(text = "notes", ids = "1,3", socialMedia = SocialMedia.TWITTER)
        val result = criteria.filter(posts, schedules)

        assertEquals(listOf("3"), result.map { it.id })
    }

    @Test
    fun `search use case reads posts and schedules from the repositories`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop"), SocialPosts(text = "release notes")))
        val schedulerRepository = InMemorySchedulerRepository()
        schedulerRepository.save(
            ScheduledItem(postsRepository.findById("2")!!, Instant.EPOCH, socialMedia = SocialMedia.LINKEDIN),
        )

        val result = Search(postsRepository, schedulerRepository).invoke(PostSearch(socialMedia = SocialMedia.LINKEDIN))

        assertEquals(1, result.size)
        assertEquals("2", result[0].id)
    }

    @Test
    fun `list formats the searched posts`() {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop"), SocialPosts(text = "release notes")))

        val result =
            List(
                postsRepository,
                InMemorySchedulerRepository(),
                MockedOutput(),
                PostSearch(text = "notes"),
            ).invoke()

        assertEquals("2. release notes (13/280)", result)
    }
}
