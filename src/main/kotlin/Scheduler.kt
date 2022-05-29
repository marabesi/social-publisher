import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "scheduler", mixinStandardHelpOptions = true, version = ["1.0"])
class Scheduler (
    @Inject
    private val postsRepository: PostsRepository
): Callable<Boolean> {
    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    lateinit var targetDate: String

    override fun call(): Boolean {
        if (postId.isNotEmpty()) {
            if (null == postsRepository.findById(postId)) {
                spec.commandLine().out.print("Couldn't find post with id 1")
                return false
            }
            spec.commandLine().out.print("Post has been scheduled")
            return true
        }

        spec.commandLine().out.print("Missing required fields")
        return false
    }
}