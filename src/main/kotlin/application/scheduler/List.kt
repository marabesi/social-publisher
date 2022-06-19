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

        for (post in findAll) {
            val isLast: Boolean = findAll.size == index
            result += if (isLast) {
                "${post.post.id}. Post with id ${post.post.id} will be published on ${post.publishDate}"
            } else {
                "${post.post.id}. Post with id ${post.post.id} will be published on ${post.publishDate}\n"
            }
            index++
        }
        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}