package application.persistence

import application.entities.ScheduledItem

interface SchedulerRepository {
    fun save(scheduledItem: ScheduledItem): Boolean
    fun findAll(): ArrayList<ScheduledItem>
    fun deleteById(id: String): ScheduledItem?
}
