package adapters.inbound.cli

import adapters.inbound.cli.scheduler.Scheduler
import adapters.inbound.cli.scheduler.SchedulerCreate
import adapters.inbound.cli.scheduler.SchedulerDelete
import adapters.inbound.cli.scheduler.SchedulerList
import adapters.outbound.csv.FileSystemConfigurationRepository
import adapters.outbound.csv.FileSystemPostRepository
import adapters.outbound.csv.FileSystemSchedulerRepository
import adapters.outbound.social.SocialSpringTwitterClient
import adapters.outbound.social.TwitterIntegration
import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.MissingConfiguration
import picocli.CommandLine
import java.time.Instant

@Suppress("UNCHECKED_CAST")
class CliFactory(
    private val currentTime: Instant,
    private val output: Output
) : CommandLine.IFactory {
    override fun <K : Any?> create(cls: Class<K>?): K {
        if (cls != null) {
            val configuration = FileSystemConfigurationRepository()
            val currentConfiguration: SocialConfiguration = try {
                configuration.find()
            } catch (_: MissingConfiguration) {
                SocialConfiguration("production", "csv", null, "UTC")
            }

            val postsRepository = FileSystemPostRepository("data/posts-${currentConfiguration.fileName}.csv")

            val filePath = "data/scheduler-${currentConfiguration.fileName}.csv"
            val scheduler = FileSystemSchedulerRepository(filePath, postsRepository)

            if (cls == Post::class.java) {
                return Post(postsRepository, output) as K
            }

            if (cls == SchedulerList::class.java) {
                return SchedulerList(scheduler, output) as K
            }

            if (cls == Scheduler::class.java) {
                return Scheduler(output) as K
            }

            if (cls == SchedulerDelete::class.java) {
                return SchedulerDelete(scheduler, output) as K
            }

            if (cls == SchedulerCreate::class.java) {
                return SchedulerCreate(postsRepository, scheduler, configuration, output) as K
            }

            if (cls == Poster::class.java) {
                return Poster(
                    scheduler,
                    output,
                    currentTime,
                    TwitterIntegration(
                        currentConfiguration,
                        SocialSpringTwitterClient(currentConfiguration)
                    ),
                ) as K
            }

            if (cls == Configuration::class.java) {
                return Configuration(output, FileSystemConfigurationRepository()) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}
