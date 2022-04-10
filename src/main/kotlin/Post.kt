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
            if (postsRepository.findAll().isEmpty()) {
                return "No post found"
            }

            return "1. this is my first post"
        }

        if (text.isNotBlank()) {
            postsRepository.save(arrayListOf(SocialPosts(text)))
            return "Post has been created"
        }
        TODO()
    }
}
