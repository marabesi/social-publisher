package application.poster

import application.Output
import application.persistence.SchedulerRepository
import application.socialnetwork.SocialThirdParty
import java.time.Instant

class Executor(
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentDate: Instant,
    private val twitterClient: SocialThirdParty
) {

    fun invoke(): String {
        val posts = schedulerRepository.findAll()
        if (posts.isEmpty()) {
            return cliOutput.write("There are no posts to be posted")
        }
        var index = 1
        var result = ""
        posts.forEach {
            val isLast: Boolean = posts.size == index
            result += if (currentDate < it.publishDate) {
                if (isLast) {
                    "Waiting for the date to come to publish post ${it.post.id}"
                } else {
                    "Waiting for the date to come to publish post ${it.post.id}\n"
                }
            } else {
                twitterClient.send(it)

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