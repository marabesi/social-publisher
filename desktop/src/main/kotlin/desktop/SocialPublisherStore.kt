package desktop

import application.Output
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration
import application.post.Create

class SocialPublisherStore(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
) {
    fun createPost(text: String): String = Create(postsRepository, output).invoke(text)

    fun posts(): ArrayList<SocialPosts> = postsRepository.findAll()

    fun deletePost(id: String): String = application.post.Delete(postsRepository, output).invoke(id)

    fun updatePost(
        id: String,
        text: String,
    ): String = application.post.Update(postsRepository, output).invoke(id, text)

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

    fun configuration(): SocialConfiguration? =
        try {
            configurationRepository.find()
        } catch (error: MissingConfiguration) {
            null
        }

    fun storeConfiguration(configuration: SocialConfiguration): String =
        application.configuration.Create(output, configurationRepository).invoke(configuration)
}
