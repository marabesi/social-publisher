package application.poster

import application.Output
import application.persistence.SchedulerRepository
import application.socialnetwork.SocialThirdParty
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Suppress("MaxLineLength")
class Executor(
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentDate: Instant,
    private val twitterClient: SocialThirdParty
) {

    fun invoke(): String {
        val posts = schedulerRepository.findAll().filter { !it.published }
        if (posts.isEmpty()) {
            return cliOutput.write("There are no posts to be posted")
        }
        var index = 1
        var result = ""
        posts.forEach {
            val isLast: Boolean = posts.size == index
            result += if (currentDate < it.publishDate) {
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")
                    .withZone(ZoneOffset.UTC)

                if (isLast) {
                    "Waiting for the date to come to publish post ${it.post.id} (scheduled for ${formatter.format(it.publishDate)})"
                } else {
                    "Waiting for the date to come to publish post ${it.post.id} (scheduled for ${formatter.format(it.publishDate)})\n"
                }
            } else {
                twitterClient.send(it)
                schedulerRepository.markAsSent(it)

                if (isLast) {
                    "Post ${it.post.id} sent to twitter"
                } else {
                    "Post ${it.post.id} sent to twitter\n"
                }
            }
            index++
        }
        return cliOutput.write(result)
    }
}
