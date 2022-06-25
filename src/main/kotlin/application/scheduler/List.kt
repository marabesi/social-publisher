package application.scheduler

import application.Output
import application.persistence.SchedulerRepository

class List(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output
) {

    fun invoke(): String {
        var result = ""
        val findAll = scheduleRepository.findAll()
        var index = 1;

        for (scheduledItem in findAll) {
            val isLast: Boolean = findAll.size == index
            result += if (isLast) {
                "${scheduledItem.id}. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}"
            } else {
                "${scheduledItem.id}. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}\n"
            }
            index++
        }

        if (findAll.isEmpty()) {
            result = "No posts scheduled"
        }

        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}