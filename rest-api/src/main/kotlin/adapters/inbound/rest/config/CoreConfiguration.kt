package adapters.inbound.rest.config

import adapters.inbound.rest.RestOutput
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.social.Twitter
import adapters.outbound.social.TwitterCredentialsValidator
import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.CreateTweet
import application.socialnetwork.SocialThirdParty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreConfiguration {
    @Bean
    fun output(): Output = RestOutput()

    @Bean
    fun configurationRepository(): ConfigurationRepository = FileSystemConfigurationRepository()

    @Bean
    fun postsRepository(configurationRepository: ConfigurationRepository): PostsRepository =
        FileSystemPostRepository(configurationRepository = configurationRepository)

    @Bean
    fun schedulerRepository(
        postsRepository: PostsRepository,
        configurationRepository: ConfigurationRepository,
    ): SchedulerRepository =
        FileSystemSchedulerRepository(
            postsRepository = postsRepository,
            configurationRepository = configurationRepository,
        )

    @Bean
    fun createTweet(configurationRepository: ConfigurationRepository): CreateTweet = Twitter(configurationRepository)

    @Bean
    fun socialThirdParty(
        configurationRepository: ConfigurationRepository,
        createTweet: CreateTweet,
    ): SocialThirdParty = TwitterCredentialsValidator(configurationRepository, createTweet)
}
