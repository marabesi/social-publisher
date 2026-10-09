package adapters.outbound.inmemory

import application.entities.ScheduledItem
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class InMemorySchedulerRepositoryTest {
    @Test
    fun `assigns incrementing schedule ids`() {
        val repository = InMemorySchedulerRepository()
        val post = SocialPosts("1", "another post")

        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T11:00:00Z")))
        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T12:00:00Z")))

        assertEquals(listOf("1", "2"), repository.findAll().map { it.id })
    }

    @Test
    fun `keeps schedule ids unique and incrementing after deleting one`() {
        val repository = InMemorySchedulerRepository()
        val post = SocialPosts("1", "another post")

        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T11:00:00Z")))
        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T12:00:00Z")))
        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T13:00:00Z")))

        repository.deleteById("2")
        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T14:00:00Z")))

        val ids = repository.findAll().map { it.id }
        assertEquals(3, ids.size)
        assertEquals(3, ids.toSet().size)
        assertEquals(listOf("1", "3", "4"), ids)
    }

    @Test
    fun `deletes the schedule by its own id instead of the post id`() {
        val repository = InMemorySchedulerRepository()
        val post = SocialPosts("1", "another post")

        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T11:00:00Z")))
        repository.save(ScheduledItem(post, Instant.parse("2021-11-25T12:00:00Z")))

        repository.deleteById("1")

        assertEquals(listOf("2"), repository.findAll().map { it.id })
    }
}
