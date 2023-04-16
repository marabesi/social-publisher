package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.Create
import application.scheduler.SocialMedia
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "create", mixinStandardHelpOptions = true)
class SchedulerCreate(
    @Inject
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val cliOutput: Output,
) : Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    var targetDate: String = ""

    @CommandLine.Option(names = ["-s"], description = ["Social media"])
    var socialMedia: SocialMedia = SocialMedia.TWITTER

    override fun call(): String {
        if (targetDate.isEmpty()) {
            return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
        }

        return Create(
            postsRepository,
            scheduleRepository,
            configurationRepository,
            cliOutput
        ).invoke(postId, targetDate)
    }
}
