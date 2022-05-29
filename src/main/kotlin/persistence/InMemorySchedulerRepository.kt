package persistence

import ScheduledItem
import SchedulerRepository
import socialPosts.SocialPosts
import java.time.Instant

class InMemorySchedulerRepository: SchedulerRepository{

    private var storedScheduler: ArrayList<ScheduledItem> = arrayListOf();
    override fun save(socialPost: SocialPosts, publishDate: Instant): Boolean {
        return storedScheduler.add(ScheduledItem(
            socialPost,
            publishDate
        ))
    }

    override fun findAll(): ArrayList<ScheduledItem> {
        return storedScheduler
    }
}