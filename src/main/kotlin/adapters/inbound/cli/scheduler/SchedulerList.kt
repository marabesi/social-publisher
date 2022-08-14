package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.List
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList(
    @Inject
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentTime: Instant
): Callable<String> {

    @CommandLine.Option(names = ["--future-only"], description = ["list posts that are beyond the future date"])
    var futureOnly: Boolean = false

    @CommandLine.Option(names = ["--group-by"], description = ["Outputs the scheduled posts grouped by a given criteria"])
    var groupBy: String = ""

    override fun call(): String {
        return List(scheduleRepository, cliOutput, currentTime, futureOnly, groupBy).invoke()
    }
}
