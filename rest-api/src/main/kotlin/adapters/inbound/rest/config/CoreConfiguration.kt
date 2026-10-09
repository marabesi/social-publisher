package adapters.inbound.rest.config

import adapters.inbound.rest.RestOutput
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.social.LinkedInOAuthClient
import adapters.outbound.social.Linkedin
import adapters.outbound.social.LinkedinCredentialsValidator
import adapters.outbound.social.SocialThirdPartyRouting
import adapters.outbound.social.Twitter
import adapters.outbound.social.TwitterCredentialsValidator
import application.Output
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.ExchangeLinkedInAuthorization
import application.socialnetwork.PublishPost
import application.socialnetwork.SocialThirdPartyProvider
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
    fun publishPost(configurationRepository: ConfigurationRepository): PublishPost = Twitter(configurationRepository)

    @Bean
    fun linkedin(configurationRepository: ConfigurationRepository): Linkedin = Linkedin(configurationRepository)

    @Bean
    fun exchangeLinkedInAuthorization(configurationRepository: ConfigurationRepository): ExchangeLinkedInAuthorization =
        LinkedInOAuthClient(configurationRepository)

    @Bean
    fun socialThirdPartyProvider(
        configurationRepository: ConfigurationRepository,
        publishPost: PublishPost,
        linkedin: Linkedin,
    ): SocialThirdPartyProvider =
        SocialThirdPartyRouting(
            mapOf(
                SocialMedia.TWITTER to TwitterCredentialsValidator(configurationRepository, publishPost),
                SocialMedia.LINKEDIN to LinkedinCredentialsValidator(configurationRepository, linkedin),
            ),
        )
}
