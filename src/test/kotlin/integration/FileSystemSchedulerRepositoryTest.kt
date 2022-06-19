package integration

import org.junit.jupiter.api.Assertions.assertEquals
import junit.framework.TestCase.assertTrue
import application.entities.ScheduledItem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import application.entities.SocialPosts
import java.io.File
import java.time.Instant

class FileSystemSchedulerRepositoryTest {
    private val filePath = "scheduler-social-publisher.csv"
    private val filePathWithSubfolder = "data/scheduler-social-publisher.csv"

    @AfterEach
    fun afterEach() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
    }

    @Test
    fun storeScheduler() {
        val post = SocialPosts("1", "another post")
        val repository = FileSystemSchedulerRepository(filePath, InMemoryPostRepository())

        assertTrue(repository.save(
            ScheduledItem(
                post, Instant.parse("2021-11-25T11:00:00Z")
            )
        ))
    }

    @Test
    fun fetchScheduledItem() {
        val post = SocialPosts("1", "another post")
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(post))

        val repository = FileSystemSchedulerRepository(filePath, postsRepository)

        repository.save(
            ScheduledItem(
                post, Instant.parse("2021-11-25T11:00:00Z")
            )
        )

        val scheduledItem: ScheduledItem = repository.findAll()[0]
        assertEquals(post.id, scheduledItem.post.id)
        assertEquals(post.text, scheduledItem.post.text)
    }
}