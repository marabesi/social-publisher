package cli

import PostsRepository
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "poster", mixinStandardHelpOptions = true)
class Poster(
    @Inject
    private val postsRepository: PostsRepository,
    private val cliOutput: Output
): Callable<String> {
    @CommandLine.Option(names = ["-p"], description = ["Post Id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-s"], description = ["Social media to post"])
    var socialMedia: String = ""

    override fun call(): String {
        return cliOutput.write("Post 1 set to twitter")
    }
}
