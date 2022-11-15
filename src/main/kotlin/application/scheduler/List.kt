package application.scheduler

import application.Output
import application.persistence.SchedulerRepository
import application.scheduler.filters.Criterion

class List(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val filters: ArrayList<Criterion>,
    private val groupBy: String
) {

    fun invoke(): String {
        var result = ""
        val findAll = scheduleRepository.findAll(filters)
        var index = 1

        for (scheduledItem in findAll) {
            val isLast: Boolean = findAll.size == index
            result += if (isLast) {
                "$index. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}"
            } else {
                "$index. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}\n"
            }
            index++
        }

        if (groupBy == "post") {
            result = ""
            index = 1
            val grouped = findAll.groupBy { it.post.id }

            grouped.forEach { t, u ->
                val isLast: Boolean = grouped.size == index
                result += if (isLast) {
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
