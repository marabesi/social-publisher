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

    @CommandLine.Option(names = ["-r"], description = ["Sets the cli to remove a scheduled post"])
    var deletes: Boolean = false

    @CommandLine.Option(names = ["-s"], description = ["Scheduled id"])
    var scheduleId: String = ""

    override fun call(): String {
        if (create) {
            return Create(postsRepository, scheduleRepository, cliOutput).invoke(postId, targetDate)
        }

        if (list) {
            return List(scheduleRepository, cliOutput).invoke()
        }

        if (deletes && scheduleId.isNotEmpty()) {
            if (scheduleRepository.deleteById(scheduleId)) {
                return cliOutput.write("Schedule 1 has been removed from post 1")
            }
        }

        return cliOutput.write("Missing required fields")
    }
}