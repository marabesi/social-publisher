package adapters.inbound.cli

import adapters.inbound.cli.scheduler.SchedulerCreate
import adapters.inbound.cli.scheduler.SchedulerCrud
import adapters.inbound.cli.scheduler.SchedulerList
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

@Suppress("UNCHECKED_CAST")
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
            val cliOutput = CliOutput()

            if (cls == Post::class.java) {
                return Post(postsRepository, cliOutput) as K
            }

            if (cls == SchedulerList::class.java) {
                return SchedulerList(scheduler, cliOutput, currentTime) as K
            }

            if (cls == SchedulerCrud::class.java) {
                return SchedulerCrud(scheduler, cliOutput) as K
            }

            if (cls == SchedulerCreate::class.java) {
                return SchedulerCreate(postsRepository, scheduler, cliOutput) as K
            }

            if (cls == Poster::class.java) {
                return Poster(
                    scheduler,
                    cliOutput,
                    currentTime,
                    TwitterIntegration(
                        currentConfiguration,
                        SocialSpringTwitterClient(currentConfiguration)
                    )
                ) as K
            }

            if (cls == Configuration::class.java) {
                return Configuration(cliOutput, FileSystemConfigurationRepository()) as K
            }
        }
        return CommandLine.defaultFactory().create(cls)
    }
}
