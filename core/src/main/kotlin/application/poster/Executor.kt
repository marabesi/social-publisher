package application.poster

import application.Output
import application.persistence.SchedulerRepository
import application.socialnetwork.SocialThirdPartyProvider
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Suppress("MaxLineLength")
class Executor(
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentDate: Instant,
    private val socialNetworks: SocialThirdPartyProvider,
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
            result +=
                if (currentDate < it.publishDate) {
                    val formatter =
                        DateTimeFormatter
                            .ofPattern("dd MMM yyyy HH:mm:ss")
                            .withZone(ZoneOffset.UTC)

                    if (isLast) {
                        "Waiting for the date to come to publish post ${it.post.id} (scheduled for ${formatter.format(it.publishDate)})"
                    } else {
                        "Waiting for the date to come to publish post ${it.post.id} (scheduled for ${formatter.format(it.publishDate)})\n"
                    }
                } else {
                    val network = it.socialMedia.displayName.lowercase()
                    socialNetworks.forMedia(it.socialMedia).send(it)
                    schedulerRepository.markAsSent(it)

                    if (isLast) {
                        "Post ${it.post.id} sent to $network"
                    } else {
                        "Post ${it.post.id} sent to $network\n"
                    }
                }
            index++
        }
        return cliOutput.write(result)
    }
}
