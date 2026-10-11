package application.post.order

import application.entities.SocialPosts
import application.order.Direction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PostIdOrderTest {
    @Test
    fun `should order posts ascending by id`() {
        val first = SocialPosts(id = "1", text = "first")
        val second = SocialPosts(id = "2", text = "second")

        val result = PostIdOrder(Direction.ASC).apply(arrayListOf(second, first))

        assertEquals(arrayListOf(first, second), result)
    }

    @Test
    fun `should order posts descending by id`() {
        val first = SocialPosts(id = "1", text = "first")
        val second = SocialPosts(id = "2", text = "second")

        val result = PostIdOrder(Direction.DESC).apply(arrayListOf(first, second))

        assertEquals(arrayListOf(second, first), result)
    }

    @Test
    fun `should order posts by the numeric value of the id ascending`() {
        val posts = listOf("30", "3", "29", "28").map { SocialPosts(id = it, text = "post $it") }

        val result = PostIdOrder(Direction.ASC).apply(ArrayList(posts))

        assertEquals(listOf("3", "28", "29", "30"), result.map { it.id })
    }

    @Test
    fun `should order posts by the numeric value of the id descending`() {
        val posts = listOf("3", "30", "28", "29").map { SocialPosts(id = it, text = "post $it") }

        val result = PostIdOrder(Direction.DESC).apply(ArrayList(posts))

        assertEquals(listOf("30", "29", "28", "3"), result.map { it.id })
    }

    @Test
    fun `should push posts without a numeric id to the end`() {
        val first = SocialPosts(id = "1", text = "first")
        val withoutId = SocialPosts(text = "no id")
        val notANumber = SocialPosts(id = "abc", text = "not a number")

        val ascending = PostIdOrder(Direction.ASC).apply(arrayListOf(notANumber, withoutId, first))
        val descending = PostIdOrder(Direction.DESC).apply(arrayListOf(notANumber, withoutId, first))

        assertEquals(listOf("1", "abc", null), ascending.map { it.id })
        assertEquals(listOf("1", "abc", null), descending.map { it.id })
    }

    @Test
    fun `should keep an empty list empty`() {
        val result = PostIdOrder(Direction.ASC).apply(arrayListOf())

        assertEquals(0, result.size)
    }
}
