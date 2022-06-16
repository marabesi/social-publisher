package application.poster

import application.Output
import application.persistence.SchedulerRepository
import java.time.Instant

class Executor(
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentDate: Instant
) {

    fun invoke(): String {
        val posts = schedulerRepository.findAll()
        if (posts.isEmpty()) {
            return cliOutput.write("There are no posts to be posted")
        }
        posts.forEach {
            if (currentDate < it.publishDate) {
                return cliOutput.write("Waiting for the date to come to publish post 1")
            }
        }
        return cliOutput.write("Post 1 sent to twitter")
    }
}