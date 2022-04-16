import socialPosts.SocialPosts
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post(
    @Inject
    private val postsRepository: PostsRepository
): Callable<String> {

    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    lateinit var text: String

    @CommandLine.Option(names = ["-l"], description = ["List created posts"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            val findAll = postsRepository.findAll()
            if (findAll.isEmpty()) {
                print("No post found")
                return "No post found"
            }

            var result = ""
            var index = 1
            for (post in findAll) {
                result += index.toString() + ". " + post.text + "\n"
                ++index
            }
            print(result)
            return result.trimIndent()
        }

        if (text.isNotBlank()) {
            postsRepository.save(arrayListOf(SocialPosts(text)))
            print("Post has been created")
            return "Post has been created"
        }
        TODO()
    }
}
