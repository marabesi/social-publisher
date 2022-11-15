package adapters.inbound.cli

import application.Output
import application.persistence.PostsRepository
import application.post.Create
import application.post.List
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post(
    @Inject
    private val postsRepository: PostsRepository,
    private val cliOutput: Output
) : Callable<String> {
    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    var text: String = ""

    @CommandLine.Option(names = ["-l"], description = ["List created posts"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            return List(postsRepository, cliOutput).invoke()
        }

        return Create(postsRepository, cliOutput).invoke(text)
    }
}
