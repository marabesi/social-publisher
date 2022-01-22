import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post: Callable<String> {
    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    lateinit var text: String

    override fun call(): String {
        println("this is my first post")
        return "Post has been created"
    }
}
