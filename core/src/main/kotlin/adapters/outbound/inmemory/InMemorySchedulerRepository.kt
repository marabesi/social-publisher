package adapters.outbound.inmemory

import application.entities.ScheduledItem
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion

class InMemorySchedulerRepository : SchedulerRepository {
    private var storedScheduler: ArrayList<ScheduledItem> = arrayListOf()

    override fun save(scheduledItem: ScheduledItem): Boolean {
        val id =
            if (scheduledItem.id.isNullOrEmpty()) {
                nextScheduleId().toString()
            } else {
                scheduledItem.id
            }
        return storedScheduler.add(
            ScheduledItem(
                scheduledItem.post,
                scheduledItem.publishDate,
                id,
                scheduledItem.published,
                scheduledItem.socialMedia,
            ),
        )
    }

    override fun findAll(filters: ArrayList<Criterion>): ArrayList<ScheduledItem> {
        val iterator = filters.iterator()
        var scheduledItems = storedScheduler
        while (iterator.hasNext()) {
            val criterion = iterator.next()
            scheduledItems = scheduledItems.filter { criterion.applyPredicateFor(it) } as ArrayList<ScheduledItem>
        }
        return scheduledItems
    }

    override fun deleteById(id: String): ScheduledItem? {
        val toBeDeleted = storedScheduler.find { it.id == id }
        storedScheduler.removeIf {
            it.id == id
        }
        return toBeDeleted
    }

    override fun markAsSent(scheduledItem: ScheduledItem): ScheduledItem {
        val toBeMarkedAsPublished = findAll().find { it.id == scheduledItem.id }
        toBeMarkedAsPublished!!.published = true

        return toBeMarkedAsPublished
    }

    private fun nextScheduleId(): Int = (storedScheduler.mapNotNull { it.id?.toIntOrNull() }.maxOrNull() ?: 0) + 1
}
