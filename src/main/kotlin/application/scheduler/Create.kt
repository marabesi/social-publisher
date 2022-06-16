package application.scheduler

import application.Output
import application.entities.ScheduledItem
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import java.time.Instant

class Create(
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output
) {

    fun invoke(postId: String, targetDate: String): String {
        if (postId.isNotEmpty() && targetDate.isNotEmpty()) {
            val post = postsRepository.findById(postId) ?: return cliOutput.write("Couldn't find post with id $postId")

            scheduleRepository.save(
                ScheduledItem(
                    post, Instant.parse(targetDate)
                )
            )
            return cliOutput.write("Post has been scheduled")
        }

        return cliOutput.write("Missing required fields")
    }
}