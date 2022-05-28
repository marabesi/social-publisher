import socialPosts.SocialPosts
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post(
    @Inject
    private val postsRepository: PostsRepository
): Callable<String> {
    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    lateinit var text: String

    @CommandLine.Option(names = ["-l"], description = ["List created posts"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            val findAll = postsRepository.findAll()
            if (findAll.isEmpty()) {
                spec.commandLine().out.print("No post found")
                return "No post found"
            }

            var result = ""
            var index = 1
            for (post in findAll) {
                val isLast: Boolean = index == findAll.size

                when {
                    post.text.length > 50 -> {
                        result += index.toString() + ". " + post.text.substring(0, 50) + "..."
                    }
                    else -> {
                        result += index.toString() + ". " + post.text
                    }
                }
                if (!isLast) {
                    result += "\n"
                }
                ++index
            }
            val output = result.trimIndent()
            spec.commandLine().out.print(output)
            return output
        }

        if (text.isNotBlank()) {
            postsRepository.save(arrayListOf(SocialPosts(text)))
            spec.commandLine().out.print("Post has been created")
            return "Post has been created"
        }
        TODO()
    }
}
