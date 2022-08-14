package application.scheduler

import application.Output
import application.entities.ScheduledItem
import application.persistence.SchedulerRepository
import java.time.Instant

class List(
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentTime: Instant,
    private val futureOnly: Boolean
) {

    fun invoke(): String {
        var result = ""
        var findAll = scheduleRepository.findAll()
        var index = 1;

        if (futureOnly) {
            findAll = findAll.filter { it.publishDate > currentTime } as ArrayList<ScheduledItem>
        }


        for (scheduledItem in findAll) {
            val isLast: Boolean = findAll.size == index
            result += if (isLast) {
                "$index. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}"
            } else {
                "$index. Post with id ${scheduledItem.post.id} will be published on ${scheduledItem.publishDate}\n"
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
