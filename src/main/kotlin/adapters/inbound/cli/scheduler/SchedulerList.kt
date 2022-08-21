package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion
import application.scheduler.List
import application.scheduler.filters.FutureOnly
import application.scheduler.filters.UntilDate
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.concurrent.Callable

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList(
    @Inject
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentTime: Instant
): Callable<String> {

    @CommandLine.Option(names = ["--start-date"], description = [
        "list posts that has they publish date starting with this value"
    ])
    var startDate: String = ""

    @CommandLine.Option(names = ["--end-date"], description = ["list posts until this date"])
    var endDate: String = ""

    @CommandLine.Option(names = ["--group-by"], description = [
        "Outputs the scheduled posts grouped by a given criteria"
    ])
    var groupBy: String = ""

    override fun call(): String {
        val filters: ArrayList<Criterion> = arrayListOf()

        if (groupBy.isNotEmpty() && groupBy != "post") {
            return cliOutput.write("Value for group-by is not valid")
        }

        if (startDate.isNotEmpty()) {
            filters.add(FutureOnly(currentTime))
        }

        if (endDate.isNotEmpty()) {
            val date: Instant
            try {
                date = Instant.parse(endDate)
            } catch (_: DateTimeParseException) {
                return cliOutput.write("Invalid date in --end-date")
            }

            filters.add(UntilDate(date))
        }

        return List(scheduleRepository, cliOutput, filters, groupBy).invoke()
    }
}
