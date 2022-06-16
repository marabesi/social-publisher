package application.persistence

import application.socialPosts.ScheduledItem

interface SchedulerRepository {
    fun save(scheduledItem: ScheduledItem): Boolean
    fun findAll(): ArrayList<ScheduledItem>
}
