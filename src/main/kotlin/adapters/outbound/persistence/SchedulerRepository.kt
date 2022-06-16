package adapters.outbound.persistence

import socialPosts.ScheduledItem

interface SchedulerRepository {
    fun save(scheduledItem: ScheduledItem): Boolean
    fun findAll(): ArrayList<ScheduledItem>
}
