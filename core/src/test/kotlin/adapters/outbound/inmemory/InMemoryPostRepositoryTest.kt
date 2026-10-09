package adapters.outbound.inmemory

import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InMemoryPostRepositoryTest {
    @Test
    fun `assigns incrementing post ids`() {
        val repository = InMemoryPostRepository()

        repository.save(arrayListOf(SocialPosts(text = "first"), SocialPosts(text = "second")))

        assertEquals(listOf("1", "2"), repository.findAll().map { it.id })
    }

    @Test
    fun `keeps post ids unique and incrementing after deleting one`() {
        val repository = InMemoryPostRepository()
        repository.save(arrayListOf(SocialPosts(text = "first")))
        repository.save(arrayListOf(SocialPosts(text = "second")))
        repository.save(arrayListOf(SocialPosts(text = "third")))

        repository.deleteById("2")
        repository.save(arrayListOf(SocialPosts(text = "fourth")))

        val ids = repository.findAll().map { it.id }
        assertEquals(3, ids.size)
        assertEquals(3, ids.toSet().size)
        assertEquals(listOf("1", "3", "4"), ids)
    }
}
