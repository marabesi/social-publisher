package cli

import PostsRepository
import SchedulerRepository
import com.google.inject.Inject
import picocli.CommandLine
import socialPosts.ScheduledItem
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "scheduler", mixinStandardHelpOptions = true, version = ["1.0"])
class Scheduler(
    @Inject
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val cliOutput: Output
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    lateinit var targetDate: String

    @CommandLine.Option(names = ["-l"], description = ["List scheduled posts"])
    var list: Boolean = false

    override fun call(): String {
        if (postId.isNotEmpty()) {
            val post = postsRepository.findById(postId)
            if (null == post) {
                return cliOutput.write("Couldn't find post with id 1")
            }

            scheduleRepository.save(
                ScheduledItem(
                    post, Instant.parse(targetDate)
                )
            )
            return cliOutput.write("Post has been scheduled")
        }

        if (list) {
            var result = ""
            var index = 1
            val findAll = scheduleRepository.findAll()
            for (post in findAll) {
                result += "$index. Post with id ${post.post.id} will be published on ${post.publishDate}"
            }
            val output = result.trimIndent()
            return cliOutput.write(output)
        }

        return cliOutput.write("Missing required fields")
    }
}