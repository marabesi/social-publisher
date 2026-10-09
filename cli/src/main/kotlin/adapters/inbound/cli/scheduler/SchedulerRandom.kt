package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.PickRandom
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "random",
    mixinStandardHelpOptions = true,
    description = ["Pick a random post and schedule it for a given day"],
)
class SchedulerRandom
    @Inject
    constructor(
        private val postsRepository: PostsRepository,
        private val scheduleRepository: SchedulerRepository,
        private val configurationRepository: ConfigurationRepository,
        private val cliOutput: Output,
        private val currentDate: Instant,
    ) : Callable<String> {
        @CommandLine.Option(
            names = ["-d"],
            description = ["Target day (e.g. 2026-10-07 or a full date time) to pick a random slot for"],
        )
        var targetDate: String = ""

        @CommandLine.Option(names = ["-s"], description = ["Social media"])
        var socialMedia: SocialMedia = SocialMedia.TWITTER

        override fun call(): String {
            if (targetDate.isEmpty()) {
                return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
            }

            return PickRandom(
                postsRepository,
                scheduleRepository,
                configurationRepository,
                cliOutput,
                now = { currentDate },
            ).invoke(targetDate, socialMedia)
        }
    }
