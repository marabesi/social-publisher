package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "delete", mixinStandardHelpOptions = true)
class SchedulerDelete(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
): Callable<String> {

    @CommandLine.Option(names = ["-id"], description = ["Scheduled id"])
    var scheduleId: String = ""

    override fun call(): String {
        if (scheduleId.isNotEmpty()) {
            val scheduledItem = scheduleRepository.deleteById(scheduleId)
            if (scheduledItem != null) {
                return cliOutput.write("Schedule $scheduleId has been removed from post ${scheduledItem.post.id}")
            }
        }

        return cliOutput.write("Missing required fields")
    }
}
