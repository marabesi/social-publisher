import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "post", helpCommand = true)
class Post: Callable<Int> {
    @CommandLine.Option(names = ["-c"], description = ["Creates a post"])
    lateinit var text: String

    override fun call(): Int {
        println("this is my first post")
        return 0
    }
}
