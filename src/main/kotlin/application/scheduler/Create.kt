package application.scheduler

import application.Output
import application.entities.ScheduledItem
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import java.time.Instant
import java.time.format.DateTimeParseException

class Create(
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output
) {

    fun invoke(postId: String, targetDate: String): String {
        if (postId.isNotEmpty() && targetDate.isNotEmpty()) {
            val post = postsRepository.findById(postId) ?: return cliOutput.write("Couldn't find post with id $postId")

            val date: Instant
            try {
                date = Instant.parse(targetDate)
            } catch (_: DateTimeParseException) {
                return cliOutput.write("Invalid date time to schedule post")
            }

            scheduleRepository.findAll().forEach {
                if (it.publishDate.toString() == targetDate && it.post.id == postId) {
                    return cliOutput.write("Post is already scheduled for $targetDate")
                }
            }

            scheduleRepository.save(
                ScheduledItem(
                    post, date
                )
            )
            return cliOutput.write("Post has been scheduled")
        }

        return cliOutput.write("Missing required fields")
    }
}
