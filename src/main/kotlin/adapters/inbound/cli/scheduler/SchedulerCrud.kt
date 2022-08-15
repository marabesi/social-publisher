package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "scheduler", mixinStandardHelpOptions = true, subcommands = [
    SchedulerList::class,
    SchedulerCreate::class
])
class SchedulerCrud(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-r"], description = ["Sets the cli to remove a scheduled post"])
    var deletes: Boolean = false

    @CommandLine.Option(names = ["-id"], description = ["Scheduled id"])
    var scheduleId: String = ""

    override fun call(): String {
        if (deletes && scheduleId.isNotEmpty()) {
            val scheduledItem = scheduleRepository.deleteById(scheduleId)
            if (scheduledItem != null) {
                return cliOutput.write("Schedule $scheduleId has been removed from post ${scheduledItem.post.id}")
            }
        }

        return cliOutput.write("Missing required fields")
    }
}
