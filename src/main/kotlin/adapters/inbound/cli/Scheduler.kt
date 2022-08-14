package adapters.inbound.cli

import adapters.inbound.cli.scheduler.SchedulerList
import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.scheduler.Create
import application.scheduler.SocialMedia
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "scheduler", mixinStandardHelpOptions = true, subcommands = [
    SchedulerList::class
])
class Scheduler(
    @Inject
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    currentTime: Instant
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    lateinit var targetDate: String

    @CommandLine.Option(names = ["-c"], description = ["Sets the cli to schedule a post"])
    var create: Boolean = false

    @CommandLine.Option(names = ["-r"], description = ["Sets the cli to remove a scheduled post"])
    var deletes: Boolean = false

    @CommandLine.Option(names = ["-s"], description = ["Social media"])
    var socialMedia: SocialMedia = SocialMedia.TWITTER

    @CommandLine.Option(names = ["-id"], description = ["Scheduled id"])
    var scheduleId: String = ""

    override fun call(): String {
        if (create) {
            return Create(postsRepository, scheduleRepository, cliOutput).invoke(postId, targetDate)
        }

        if (deletes && scheduleId.isNotEmpty()) {
            val scheduledItem = scheduleRepository.deleteById(scheduleId)
            if (scheduledItem != null) {
                return cliOutput.write("Schedule $scheduleId has been removed from post ${scheduledItem.post.id}")
            }
        }

        return cliOutput.write("Missing required fields")
    }
}
