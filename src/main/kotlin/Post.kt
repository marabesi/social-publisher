import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", mixinStandardHelpOptions = true)
class Post: Callable<String> {

    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    lateinit var text: String

    @CommandLine.Option(names = ["-l"], description = ["List created posts"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            return "No post found"
        }
        return "Post has been created"
    }
}
