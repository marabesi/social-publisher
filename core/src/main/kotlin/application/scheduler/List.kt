package application.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion
import application.scheduler.order.Order

class List(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val filters: ArrayList<Criterion>,
    private val groupBy: String,
    private val order: Order? = null,
    private val search: ScheduleSearch = ScheduleSearch(),
) {
    fun invoke(): String {
        var result = ""
        val filtered = scheduleRepository.findAll(filters)
        val searched = ArrayList(search.filter(filtered))
        val findAll = order?.apply(searched) ?: searched
        var index = 1

        for (scheduledItem in findAll) {
            val isLast: Boolean = findAll.size == index
            val line =
                "$index. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate} " +
                    "(${scheduledItem.socialMedia.displayName})"
            result += if (isLast) line else "$line\n"
            index++
        }

        if (groupBy == "post") {
            result = ""
            index = 1
            val grouped = findAll.groupBy { it.post.id }

            grouped.forEach { t, u ->
                val isLast: Boolean = grouped.size == index
                result +=
                    if (isLast) {
                        "$index. Post with id $t posted ${u.size} time(s)"
                    } else {
                        "$index. Post with id $t posted ${u.size} time(s)\n"
                    }
                index++
            }
        }

        if (findAll.isEmpty()) {
            result = "No posts scheduled"
        }

        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}
