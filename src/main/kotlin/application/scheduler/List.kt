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
        for (post in findAll) {
            result += "${post.post.id}. Post with id ${post.post.id} will be published on ${post.publishDate}"
        }
        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}