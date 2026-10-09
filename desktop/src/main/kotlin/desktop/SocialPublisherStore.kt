package desktop

import application.Output
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration
import application.post.Create
import application.socialnetwork.AuthorizationUrl
import application.socialnetwork.ConnectLinkedIn
import application.socialnetwork.ExchangeLinkedInAuthorization
import application.socialnetwork.SocialThirdPartyProvider
import java.time.Instant

class SocialPublisherStore(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
    private val socialNetworks: SocialThirdPartyProvider,
    private val exchangeLinkedIn: ExchangeLinkedInAuthorization =
        ExchangeLinkedInAuthorization { error("LinkedIn OAuth exchange is not configured") },
    private val currentDate: () -> Instant = { Instant.now() },
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
        socialMedia: SocialMedia = SocialMedia.TWITTER,
    ): String {
        val create = application.scheduler.Create(postsRepository, schedulerRepository, configurationRepository, output)
        return create.invoke(postId, publishDate, socialMedia)
    }

    fun randomSchedule(
        publishDate: String,
        socialMedia: SocialMedia = SocialMedia.TWITTER,
    ): String {
        val pickRandom =
            application.scheduler.PickRandom(
                postsRepository,
                schedulerRepository,
                configurationRepository,
                output,
                now = currentDate,
            )
        return pickRandom.invoke(publishDate, socialMedia)
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

    fun linkedInAuthorizationUrl(state: String = AuthorizationUrl.DEFAULT_STATE): String =
        AuthorizationUrl(configurationRepository, output).invoke(state)

    fun connectLinkedIn(code: String): String {
        val connect = ConnectLinkedIn(configurationRepository, exchangeLinkedIn, output)
        return connect.invoke(code)
    }

    fun runPoster(): String {
        val executor = application.poster.Executor(schedulerRepository, output, currentDate(), socialNetworks)
        return executor.invoke()
    }
}
