package adapters.inbound.cli

import Main
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.social.TweetCreated
import adapters.outbound.social.TweetCreatedInterceptor
import adapters.outbound.social.Twitter
import adapters.outbound.social.TwitterCredentialsValidator
import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.CreateTweet
import application.socialnetwork.SocialThirdParty
import com.google.inject.AbstractModule
import com.google.inject.ConfigurationException
import com.google.inject.Guice
import com.google.inject.matcher.Matchers.annotatedWith
import com.google.inject.matcher.Matchers.any
import picocli.CommandLine
import java.time.Instant

class TweetCreatedModule(
    private val isInTestMode: Boolean,
    private val currentTime: Instant,
    private val output: Output,
) : AbstractModule() {
    private val configuration = FileSystemConfigurationRepository()

    override fun configure() {
        val interceptor = TweetCreatedInterceptor(configuration, isInTestMode)
        bindInterceptor(any(), annotatedWith(TweetCreated::class.java), interceptor)

        bind(Instant::class.java).toInstance(currentTime)

        bind(Main::class.java).toInstance(Main())
        bind(Output::class.java).toInstance(output)

        bind(ConfigurationRepository::class.java).toInstance(configuration)

        val postRepository = FileSystemPostRepository("data/posts-production.csv")
        bind(PostsRepository::class.java).toInstance(postRepository)

        val scheduler = FileSystemSchedulerRepository("data/scheduler-production.csv", postRepository)
        bind(SchedulerRepository::class.java).toInstance(scheduler)

        bind(SocialThirdParty::class.java).to(TwitterCredentialsValidator::class.java)
        bind(CreateTweet::class.java).to(Twitter::class.java)
    }
}

@Suppress("UNCHECKED_CAST", "SwallowedException")
class CliFactory(
    currentTime: Instant,
    output: Output,
    isInTestMode: Boolean
) : CommandLine.IFactory {
    private val injector = Guice.createInjector(
        TweetCreatedModule(isInTestMode, currentTime, output)
    )

    override fun <K : Any?> create(cls: Class<K>?): K {
        return try {
            injector.getInstance(cls)
        } catch (ex: ConfigurationException) {
            print(ex)
            return CommandLine.defaultFactory().create(cls)
        }
    }
}
