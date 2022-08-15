package adapters.outbound.inmemory

import application.entities.ScheduledItem
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion
import application.scheduler.filters.Filter
import java.time.Instant

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

    override fun findAll(filters: ArrayList<Criterion>): ArrayList<ScheduledItem> {
        val iterator = filters.iterator()
        var scheduledItems = storedScheduler
        while (iterator.hasNext()) {
            val parse: Filter = iterator.next().apply()

            scheduledItems = scheduledItems.filter {
                if (parse.key == "publishDate" && parse.predicate == ">=") {
                    val date = parse.value as Instant
                    it.publishDate >= date
                } else {
                    false
                }
            } as ArrayList<ScheduledItem>
        }
        return scheduledItems
    }

    override fun deleteById(id: String): ScheduledItem? {
        val toBeDeleted = findAll().find { it.id == id }
        storedScheduler.removeIf {
            it.post.id == id
        }
        return toBeDeleted
    }

    override fun markAsSent(scheduledItem: ScheduledItem): ScheduledItem {
        val toBeMarkedAsPublished = findAll().find { it.id == scheduledItem.id }
        toBeMarkedAsPublished!!.published = true

        return toBeMarkedAsPublished
    }
}
