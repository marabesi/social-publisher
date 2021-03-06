package adapters.inbound.cli

import adapters.outbound.cli.CliOutput
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.social.SocialSpringTwitterClient
import adapters.outbound.social.TwitterIntegration
import application.entities.SocialConfiguration
import application.persistence.configuration.MissingConfiguration
import picocli.CommandLine
import java.time.Instant

class CliFactory(
    private val currentTime: Instant
): CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            val configuration = FileSystemConfigurationRepository()
            val currentConfiguration: SocialConfiguration = try {
                configuration.find()
            } catch (_: MissingConfiguration) {
                SocialConfiguration("production", "csv")
            }

            val postsRepository = FileSystemPostRepository("data/posts-${currentConfiguration.fileName}.csv")

            val filePath = "data/scheduler-${currentConfiguration.fileName}.csv"
            val scheduler = FileSystemSchedulerRepository(filePath, postsRepository)

            if (cls == Post::class.java) {
                return Post(postsRepository, CliOutput()) as K
            }
            if (cls == Scheduler::class.java) {
                return Scheduler(postsRepository, scheduler, CliOutput()) as K
            }
            if (cls == Poster::class.java) {
                return Poster(
                    scheduler,
                    CliOutput(),
                    currentTime,
                    TwitterIntegration(
                        currentConfiguration,
                        SocialSpringTwitterClient(currentConfiguration)
                    )
                ) as K
            }

            if (cls == Configuration::class.java) {
                return Configuration(CliOutput(), FileSystemConfigurationRepository()) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}