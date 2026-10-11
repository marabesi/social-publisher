package adapters.inbound.rest.config

import adapters.inbound.rest.RestOutput
import adapters.outbound.SocialThirdPartyRouting
import adapters.outbound.bluesky.Bluesky
import adapters.outbound.bluesky.BlueskyCredentialsValidator
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.googledrive.GoogleDriveFileSync
import adapters.outbound.googledrive.GoogleDrivePostsRepository
import adapters.outbound.googledrive.GoogleDriveSchedulerRepository
import adapters.outbound.googledrive.GoogleDriveSync
import adapters.outbound.linkedin.LinkedInOAuthClient
import adapters.outbound.linkedin.Linkedin
import adapters.outbound.linkedin.LinkedinCredentialsValidator
import adapters.outbound.notion.NotionFileSync
import adapters.outbound.notion.NotionPostsRepository
import adapters.outbound.notion.NotionSchedulerRepository
import adapters.outbound.notion.NotionSync
import adapters.outbound.twitter.Twitter
import adapters.outbound.twitter.TwitterCredentialsValidator
import application.Output
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.PublishPost
import application.socialnetwork.SocialThirdPartyProvider
import application.socialnetwork.linkedin.ExchangeLinkedInAuthorization
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoreConfiguration {
    @Bean
    fun output(): Output = RestOutput()

    @Bean
    fun configurationRepository(): ConfigurationRepository = FileSystemConfigurationRepository()

    @Bean
    fun notionSync(configurationRepository: ConfigurationRepository): NotionSync = NotionFileSync(configurationRepository)

    @Bean
    fun googleDriveSync(configurationRepository: ConfigurationRepository): GoogleDriveSync = GoogleDriveFileSync(configurationRepository)

    @Bean
    fun postsRepository(
        configurationRepository: ConfigurationRepository,
        notionSync: NotionSync,
        googleDriveSync: GoogleDriveSync,
    ): PostsRepository =
        NotionPostsRepository(
            GoogleDrivePostsRepository(
                FileSystemPostRepository(configurationRepository = configurationRepository),
                googleDriveSync,
            ),
            notionSync,
        )

    @Bean
    fun schedulerRepository(
        configurationRepository: ConfigurationRepository,
        notionSync: NotionSync,
        googleDriveSync: GoogleDriveSync,
    ): SchedulerRepository =
        NotionSchedulerRepository(
            GoogleDriveSchedulerRepository(
                FileSystemSchedulerRepository(
                    postsRepository = FileSystemPostRepository(configurationRepository = configurationRepository),
                    configurationRepository = configurationRepository,
                ),
                googleDriveSync,
            ),
            notionSync,
        )

    @Bean
    fun publishPost(configurationRepository: ConfigurationRepository): PublishPost = Twitter(configurationRepository)

    @Bean
    fun linkedin(configurationRepository: ConfigurationRepository): Linkedin = Linkedin(configurationRepository)

    @Bean
    fun bluesky(configurationRepository: ConfigurationRepository): Bluesky = Bluesky(configurationRepository)

    @Bean
    fun exchangeLinkedInAuthorization(configurationRepository: ConfigurationRepository): ExchangeLinkedInAuthorization =
        LinkedInOAuthClient(configurationRepository)

    @Bean
    fun socialThirdPartyProvider(
        configurationRepository: ConfigurationRepository,
        publishPost: PublishPost,
        linkedin: Linkedin,
        bluesky: Bluesky,
    ): SocialThirdPartyProvider =
        SocialThirdPartyRouting(
            mapOf(
                SocialMedia.TWITTER to TwitterCredentialsValidator(configurationRepository, publishPost),
                SocialMedia.LINKEDIN to LinkedinCredentialsValidator(configurationRepository, linkedin),
                SocialMedia.BLUESKY to BlueskyCredentialsValidator(configurationRepository, bluesky),
            ),
        )
}
