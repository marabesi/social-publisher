package desktop

import application.Output
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.post.Create

class SocialPublisherStore(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
) {
    fun createPost(text: String): String = Create(postsRepository, output).invoke(text)

    fun posts(): ArrayList<SocialPosts> = postsRepository.findAll()

    fun schedule(
        postId: String,
        publishDate: String,
    ): String {
        val create = application.scheduler.Create(postsRepository, schedulerRepository, configurationRepository, output)
        return create.invoke(postId, publishDate)
    }

    fun schedules(): ArrayList<ScheduledItem> = schedulerRepository.findAll()

    fun deleteSchedule(id: String) {
        schedulerRepository.deleteById(id)
    }

    fun storeConfiguration(json: String): String {
        val create = application.configuration.Create(output, configurationRepository)
        return create.invoke(json)
    }
}
