package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.List
import application.scheduler.filters.Criterion
import application.scheduler.filters.DateTimeValidation
import application.scheduler.filters.StartDate
import application.scheduler.filters.UntilDate
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList @Inject constructor(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
) : Callable<String> {

    @CommandLine.Option(
        names = ["--start-date"],
        description = [
            "list posts that has they publish date starting with this value"
        ]
    )
    var startDate: String = ""

    @CommandLine.Option(names = ["--end-date"], description = ["list posts until this date"])
    var endDate: String = ""

    @CommandLine.Option(
        names = ["--group-by"],
        description = [
            "Outputs the scheduled posts grouped by a given criteria"
        ]
    )
    var groupBy: String = ""

    override fun call(): String {
        val filters: ArrayList<Criterion> = arrayListOf()

        if (groupBy.isNotEmpty() && groupBy != "post") {
            return cliOutput.write(Messages.INVALID_GROUP_BY_PARAMETER)
        }

        if (startDate.isNotEmpty()) {
            val validStartDate = DateTimeValidation(startDate)
            if (!validStartDate.isDateTimeValid()) {
                return cliOutput.write(Messages.INVALID_START_DATE)
            }

            filters.add(StartDate(validStartDate.value()))
        }

        if (endDate.isNotEmpty()) {
            val validEndDate = DateTimeValidation(endDate)
            if (!validEndDate.isDateTimeValid()) {
                return cliOutput.write(Messages.INVALID_END_DATE)
            }

            filters.add(UntilDate(validEndDate.value()))
        }

        return List(scheduleRepository, cliOutput, filters, groupBy).invoke()
    }
}
