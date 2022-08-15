package application.persistence

import application.entities.ScheduledItem
import application.scheduler.filters.Criterion

interface SchedulerRepository {
    fun save(scheduledItem: ScheduledItem): Boolean
    fun findAll(filters: ArrayList<Criterion> = arrayListOf()): ArrayList<ScheduledItem>
    fun deleteById(id: String): ScheduledItem?
    fun markAsSent(scheduledItem: ScheduledItem): ScheduledItem
}
