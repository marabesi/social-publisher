package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import application.Timezone
import application.entities.SocialMedia
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.List
import application.scheduler.ScheduleSearch
import application.scheduler.filters.Criterion
import application.scheduler.filters.DateTimeValidation
import application.scheduler.filters.FilterExpression
import application.scheduler.filters.StartDate
import application.scheduler.filters.UntilDate
import application.scheduler.order.OrderExpression
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList
    @Inject
    constructor(
        private val scheduleRepository: SchedulerRepository,
        private val configurationRepository: ConfigurationRepository,
        private val cliOutput: Output,
    ) : Callable<String> {
        @CommandLine.Option(
            names = ["--start-date"],
            description = [
                "list posts that has they publish date starting with this value",
            ],
        )
        var startDate: String = ""

        @CommandLine.Option(names = ["--end-date"], description = ["list posts until this date"])
        var endDate: String = ""

        @CommandLine.Option(
            names = ["-f", "--filter"],
            description = ["Filters the scheduled posts by any property, e.g. post.text=draft"],
        )
        var filter: String = ""

        @CommandLine.Option(
            names = ["--group-by"],
            description = [
                "Outputs the scheduled posts grouped by a given criteria",
            ],
        )
        var groupBy: String = ""

        @CommandLine.Option(
            names = ["-o", "--order-by"],
            description = [
                "Orders the scheduled posts by publish_date asc or desc",
            ],
        )
        var orderBy: String = ""

        @CommandLine.Option(
            names = ["--search"],
            description = ["Only schedules whose post matches the case-insensitive text search"],
        )
        var search: String = ""

        @CommandLine.Option(
            names = ["--ids"],
            description = ["Only schedules for the comma separated post ids"],
        )
        var ids: String = ""

        @CommandLine.Option(
            names = ["--social-media"],
            description = ["Only schedules for the given social media"],
        )
        var socialMedia: SocialMedia? = null

        override fun call(): String {
            val filters: ArrayList<Criterion> = arrayListOf()
            val zoneId = Timezone.zoneId(configurationRepository)

            if (groupBy.isNotEmpty() && groupBy != "post") {
                return cliOutput.write(Messages.INVALID_GROUP_BY_PARAMETER)
            }

            if (startDate.isNotEmpty()) {
                val validStartDate = DateTimeValidation(startDate, zoneId)
                if (!validStartDate.isDateTimeValid()) {
                    return cliOutput.write(Messages.INVALID_START_DATE)
                }

                filters.add(StartDate(validStartDate.value()))
            }

            if (endDate.isNotEmpty()) {
                val validEndDate = DateTimeValidation(endDate, zoneId)
                if (!validEndDate.isDateTimeValid()) {
                    return cliOutput.write(Messages.INVALID_END_DATE)
                }

                filters.add(UntilDate(validEndDate.value()))
            }

            if (filter.isNotEmpty()) {
                val filterCriteria = FilterExpression.parse(filter)
                if (filterCriteria == null) {
                    return cliOutput.write(Messages.INVALID_FILTER_PARAMETER)
                }

                filters.addAll(filterCriteria)
            }

            val order = OrderExpression.parse(orderBy)
            if (orderBy.isNotEmpty() && order == null) {
                return cliOutput.write(Messages.INVALID_ORDER_BY_PARAMETER)
            }

            val searchCriteria = ScheduleSearch.from(text = search, postIds = ids, socialMedia = socialMedia)

            return List(scheduleRepository, cliOutput, filters, groupBy, order, searchCriteria).invoke()
        }
    }
