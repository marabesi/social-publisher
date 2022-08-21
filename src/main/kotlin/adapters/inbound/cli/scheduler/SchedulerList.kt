package adapters.inbound.cli.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.List
import application.scheduler.filters.Criterion
import application.scheduler.filters.DateTimeValidation
import application.scheduler.filters.FutureOnly
import application.scheduler.filters.UntilDate
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList(
    @Inject
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
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
            val validStartDate = DateTimeValidation(startDate)
            if (!validStartDate.isDateTimeValid()) {
                return cliOutput.write("Invalid start date")
            }

            filters.add(FutureOnly(validStartDate.value()))
        }

        if (endDate.isNotEmpty()) {
            val validEndDate = DateTimeValidation(endDate)
            if (!validEndDate.isDateTimeValid()) {
                return cliOutput.write("Invalid end date")
            }

            filters.add(UntilDate(validEndDate.value()))
        }

        return List(scheduleRepository, cliOutput, filters, groupBy).invoke()
    }
}
