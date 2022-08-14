package integration

import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import application.entities.ScheduledItem
import application.entities.SocialPosts
import junit.framework.TestCase.assertTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Instant

class FileSystemSchedulerRepositoryTest {
    private val filePath = "scheduler-social-publisher.csv"
    private val filePathWithSubfolder = "data/scheduler-social-publisher.csv"

    @BeforeEach
    fun beforeEach() {
        File(filePath).delete()
        File(filePathWithSubfolder).delete()
    }

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
                post, Instant.parse("2021-11-25T11:00:00Z"), published = true
            )
        )

        val scheduledItem: ScheduledItem = repository.findAll()[0]
        assertEquals(post.id, scheduledItem.post.id)
        assertEquals(post.text, scheduledItem.post.text)
        assertEquals("1", scheduledItem.id)
        assertEquals(true, scheduledItem.published)
    }

    @Test
    fun ensureFileExistsWhenCallingFetchScheduledItems() {
        val repository = FileSystemSchedulerRepository(filePath, InMemoryPostRepository())

        val scheduledItems = repository.findAll()

        assertEquals(0, scheduledItems.size)
    }

    @Test
    fun shouldDeleteScheduledItem() {
        val post = SocialPosts( text = "another post")
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(post))

        val fileSystemRepository = FileSystemSchedulerRepository(filePath, postsRepository)

        fileSystemRepository.save(
            ScheduledItem(
                post, Instant.parse("2021-11-25T11:00:00Z")
            )
        )
        fileSystemRepository.save(
            ScheduledItem(
                post, Instant.parse("2021-11-26T11:00:00Z")
            )
        )
        fileSystemRepository.save(
            ScheduledItem(
                post, Instant.parse("2021-11-26T11:00:00Z")
            )
        )

        fileSystemRepository.deleteById("2")

        assertEquals(2, fileSystemRepository.findAll().size)
        assertEquals("1", fileSystemRepository.findAll()[0].id)
        assertEquals("3", fileSystemRepository.findAll()[1].id)
    }

    @Test
    fun markScheduledItemAsPublished() {
        val post = SocialPosts(text = "random post")
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(post))

        val repository = FileSystemSchedulerRepository(filePath, postsRepository)
        repository.save(ScheduledItem(post, Instant.now(), "1", false))

        repository.markAsSent(repository.findAll()[0])

        assertEquals(1, repository.findAll().size)
        assertEquals(true, repository.findAll()[0].published)
    }

    @Test
    fun markManyScheduledItemsAsPublished() {
        val post = SocialPosts(text = "random post")
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(post))

        val repository = FileSystemSchedulerRepository(filePath, postsRepository)
        val scheduledItem = ScheduledItem(post, Instant.now(), "1", false)
        repository.save(scheduledItem)
        val scheduledItem1 = ScheduledItem(post, Instant.now(), "2", false)
        repository.save(scheduledItem1)

        assertEquals(2, repository.findAll().size)

        repository.markAsSent(scheduledItem)
        repository.markAsSent(scheduledItem1)

        assertEquals(2, repository.findAll().size)
        assertEquals(true, repository.findAll()[0].published)
        assertEquals(true, repository.findAll()[1].published)
    }
}
