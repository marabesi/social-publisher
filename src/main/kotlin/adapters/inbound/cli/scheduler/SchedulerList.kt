package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.List
import application.scheduler.filters.Criterion
import application.scheduler.filters.DateTimeValidation
import application.scheduler.filters.PropertyFilter
import application.scheduler.filters.ScheduledItemProperties
import application.scheduler.filters.StartDate
import application.scheduler.filters.UntilDate
import application.scheduler.order.Direction
import application.scheduler.order.Order
import application.scheduler.order.PublishDateOrder
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

private const val PUBLISH_DATE_ORDER_FIELD = "publish_date"
private const val ORDER_ASC = "asc"
private const val ORDER_DESC = "desc"
private const val FILTER_SEPARATOR = "&"
private const val FILTER_VALUE_SEPARATOR = "="

@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
open class SchedulerList
    @Inject
    constructor(
        private val scheduleRepository: SchedulerRepository,
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

            if (filter.isNotEmpty()) {
                val filterCriteria = criteriaFrom(filter)
                if (filterCriteria == null) {
                    return cliOutput.write(Messages.INVALID_FILTER_PARAMETER)
                }

                filters.addAll(filterCriteria)
            }

            val order = orderFrom(orderBy)
            if (orderBy.isNotEmpty() && order == null) {
                return cliOutput.write(Messages.INVALID_ORDER_BY_PARAMETER)
            }

            return List(scheduleRepository, cliOutput, filters, groupBy, order).invoke()
        }

        private fun criteriaFrom(filter: String): ArrayList<Criterion>? {
            val criteria = arrayListOf<Criterion>()
            val availableKeys = ScheduledItemProperties.availableKeys()

            for (expression in filter.split(FILTER_SEPARATOR)) {
                val parts = expression.split(FILTER_VALUE_SEPARATOR)
                if (parts.size != 2 || !availableKeys.contains(parts[0])) {
                    return null
                }

                criteria.add(PropertyFilter(parts[0], parts[1]))
            }

            return criteria
        }

        private fun orderFrom(orderBy: String): Order? {
            val parts = orderBy.split("=")
            if (parts.size != 2 || parts[0] != PUBLISH_DATE_ORDER_FIELD) {
                return null
            }

            val direction =
                when (parts[1]) {
                    ORDER_ASC -> Direction.ASC
                    ORDER_DESC -> Direction.DESC
                    else -> return null
                }

            return PublishDateOrder(direction)
        }
    }
