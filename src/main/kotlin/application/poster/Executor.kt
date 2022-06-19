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
            if (currentDate < it.publishDate) {
                val isLast: Boolean = posts.size == index
                result += if (isLast) {
                    "Waiting for the date to come to publish post ${it.post.id}"
                } else {
                    "Waiting for the date to come to publish post ${it.post.id}\n"
                }
                index++
            } else {
                twitterClient.send(it)

                return cliOutput.write("Post ${it.post.id} sent to twitter")
            }
        }
        return cliOutput.write(result)
    }
}