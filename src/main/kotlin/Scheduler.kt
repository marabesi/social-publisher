import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "schedule", mixinStandardHelpOptions = true, version = ["1.0"])
class Scheduler (
    @Inject
    private val postsRepository: PostsRepository
): Callable<String> {
    @CommandLine.Spec
    lateinit var spec: CommandLine.Model.CommandSpec

    @CommandLine.Option(names = ["-p"], description = ["Post id"])
    lateinit var postId: String

    @CommandLine.Option(names = ["-d"], description = ["Target date"])
    lateinit var targetDate: String

    override fun call(): String {
        if (null == postsRepository.findById(postId)) {
            spec.commandLine().out.print("Couldn't find post with id 1")
            return ""
        }
        spec.commandLine().out.print("Post has been scheduled")
        return ""
    }
}