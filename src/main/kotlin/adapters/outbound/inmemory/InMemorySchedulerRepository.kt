package adapters.outbound.inmemory

import application.entities.ScheduledItem
import application.persistence.SchedulerRepository

class InMemorySchedulerRepository: SchedulerRepository {

    private var storedScheduler: ArrayList<ScheduledItem> = arrayListOf();
    override fun save(scheduledItem: ScheduledItem): Boolean {
        val id = findAll().size + 1
        return storedScheduler.add(
            ScheduledItem(
                scheduledItem.post,
                scheduledItem.publishDate,
                id.toString(),
            )
        )
    }

    override fun findAll(): ArrayList<ScheduledItem> {
        return storedScheduler
    }

    override fun deleteById(id: String): ScheduledItem? {
        val toBeDeleted = findAll().find { it.id == id }
        storedScheduler.removeIf {
            it.post.id == id
        }
        return toBeDeleted
    }
}