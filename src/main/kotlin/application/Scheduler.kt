package application

import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import com.google.inject.Inject
import picocli.CommandLine
import application.entities.ScheduledItem
import java.time.Instant
import java.util.concurrent.Callable
import application.scheduler.List

@CommandLine.Command(name = "scheduler", mixinStandardHelpOptions = true)
class Scheduler(
    @Inject
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    lateinit var targetDate: String

    @CommandLine.Option(names = ["-l"], description = ["List scheduled posts"])
    var list: Boolean = false

    @CommandLine.Option(names = ["-c"], description = ["Sets the cli to schedule a post"])
    var create: Boolean = false

    override fun call(): String {
        if (create && postId.isNotEmpty() && targetDate.isNotEmpty()) {
            val post = postsRepository.findById(postId) ?: return cliOutput.write("Couldn't find post with id $postId")

            scheduleRepository.save(
                ScheduledItem(
                    post, Instant.parse(targetDate)
                )
            )
            return cliOutput.write("Post has been scheduled")
        }

        if (list) {
            return List(scheduleRepository, cliOutput).invoke()
        }

        return cliOutput.write("Missing required fields")
    }
}