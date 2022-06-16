package application

import application.persistence.PostsRepository
import application.post.Create
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post(
    @Inject
    private val postsRepository: PostsRepository,
    private val cliOutput: Output
): Callable<String> {
    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    var text: String = ""

    @CommandLine.Option(names = ["-l"], description = ["List created posts"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            val findAll = postsRepository.findAll()
            if (findAll.isEmpty()) {
                return cliOutput.write("No post found")
            }

            var result = ""
            var index = 1
            for (post in findAll) {
                val isLast: Boolean = index == findAll.size

                result += when {
                    post.text.length > 50 -> {
                        post.id.toString() + ". " + post.text.substring(0, 50) + "..."
                    }
                    else -> {
                        post.id.toString() + ". " + post.text
                    }
                }
                if (!isLast) {
                    result += "\n"
                }
                ++index
            }
            val output = result.trimIndent()
            return cliOutput.write(output)
        }

        return Create(postsRepository, cliOutput).invoke(text)
    }
}
