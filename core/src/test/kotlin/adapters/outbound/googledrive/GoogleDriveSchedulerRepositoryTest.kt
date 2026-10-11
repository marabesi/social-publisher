package adapters.outbound.googledrive

import adapters.outbound.inmemory.InMemorySchedulerRepository
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.SchedulerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.Instant

class GoogleDriveSchedulerRepositoryTest {
    private class RecordingSync : GoogleDriveSync {
        var calls: Int = 0

        override fun sync() {
            ++calls
        }
    }

    private val scheduledItem =
        ScheduledItem(
            SocialPosts("1", "hello"),
            Instant.parse("2014-12-22T10:15:30Z"),
        )

    @Test
    fun `uploads after every mutation`() {
        val sync = RecordingSync()
        val repository = GoogleDriveSchedulerRepository(InMemorySchedulerRepository(), sync)

        repository.save(scheduledItem)
        assertEquals(1, sync.calls)

        val stored = repository.findAll().first()
        repository.markAsSent(stored)
        assertEquals(2, sync.calls)

        repository.deleteById("1")
        assertEquals(3, sync.calls)
    }

    @Test
    fun `does not upload when only reading`() {
        val sync = RecordingSync()
        val repository: SchedulerRepository = GoogleDriveSchedulerRepository(InMemorySchedulerRepository(), sync)

        assertEquals(0, sync.calls)
        assertDoesNotThrow { repository.findAll() }
        assertEquals(0, sync.calls)
    }
}
