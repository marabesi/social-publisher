import socialPosts.SocialPosts
import java.time.Instant

interface SchedulerRepository {
    fun save(socialPost: SocialPosts, publishDate: Instant): Boolean
    fun findAll(): ArrayList<ScheduledItem>
}
