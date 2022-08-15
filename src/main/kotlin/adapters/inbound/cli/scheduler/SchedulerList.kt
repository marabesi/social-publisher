package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion
import application.scheduler.List
import application.scheduler.filters.FutureOnly
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

    @CommandLine.Option(names = ["--group-by"], description = [
        "Outputs the scheduled posts grouped by a given criteria"
    ])
    var groupBy: String = ""

    override fun call(): String {
        val filters: ArrayList<Criterion> = arrayListOf()

        if (groupBy.isNotEmpty() && groupBy != "post") {
            return cliOutput.write("Value for group-by is not valid")
        }

        if (futureOnly) {
            filters.add(FutureOnly(currentTime))
        }

        return List(scheduleRepository, cliOutput, filters, groupBy).invoke()
    }
}
