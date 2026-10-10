package adapters.outbound.notion

import adapters.outbound.inmemory.InMemoryPostRepository
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NotionPostsRepositoryTest {
    private class RecordingSync : NotionSync {
        var calls: Int = 0

        override fun sync() {
            ++calls
        }
    }

    @Test
    fun `uploads after every mutation`() {
        val sync = RecordingSync()
        val repository = NotionPostsRepository(InMemoryPostRepository(), sync)

        repository.save(arrayListOf(SocialPosts(text = "hello")))
        assertEquals(1, sync.calls)

        val post = repository.findAll().first()
        repository.update(SocialPosts(post.id, text = "updated"))
        assertEquals(2, sync.calls)

        repository.deleteById(post.id!!)
        assertEquals(3, sync.calls)
    }

    @Test
    fun `does not upload when only reading`() {
        val sync = RecordingSync()
        val repository = NotionPostsRepository(InMemoryPostRepository(), sync)

        repository.save(arrayListOf(SocialPosts(text = "hello")))
        repository.findAll()
        repository.findById("1")

        assertEquals(1, sync.calls)
    }
}
