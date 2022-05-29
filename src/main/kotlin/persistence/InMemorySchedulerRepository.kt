package persistence

import socialPosts.ScheduledItem
import SchedulerRepository

class InMemorySchedulerRepository: SchedulerRepository{

    private var storedScheduler: ArrayList<ScheduledItem> = arrayListOf();
    override fun save(scheduledItem: ScheduledItem): Boolean {
        return storedScheduler.add(
            ScheduledItem(
            scheduledItem.post,
            scheduledItem.publishDate
        )
        )
    }

    override fun findAll(): ArrayList<ScheduledItem> {
        return storedScheduler
    }
}