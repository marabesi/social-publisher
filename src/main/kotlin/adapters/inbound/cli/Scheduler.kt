package adapters.inbound.cli

import application.Output
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.scheduler.Create
import application.scheduler.List
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

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
        if (create) {
            return Create(postsRepository, scheduleRepository, cliOutput).invoke(postId, targetDate)
        }

        if (list) {
            return List(scheduleRepository, cliOutput).invoke()
        }

        return cliOutput.write("Missing required fields")
    }
}