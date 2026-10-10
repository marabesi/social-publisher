package adapters.outbound.notion

import application.entities.ScheduledItem
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion

class NotionSchedulerRepository(
    private val delegate: SchedulerRepository,
    private val sync: NotionSync,
) : SchedulerRepository {
    override fun save(scheduledItem: ScheduledItem): Boolean = delegate.save(scheduledItem).also { sync.sync() }

    override fun findAll(filters: ArrayList<Criterion>): ArrayList<ScheduledItem> = delegate.findAll(filters)

    override fun deleteById(id: String): ScheduledItem? = delegate.deleteById(id).also { sync.sync() }

    override fun markAsSent(scheduledItem: ScheduledItem): ScheduledItem {
        val sent = delegate.markAsSent(scheduledItem)
        sync.sync()
        return sent
    }
}
