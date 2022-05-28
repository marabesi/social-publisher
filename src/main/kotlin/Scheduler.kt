import com.google.inject.Inject
import picocli.CommandLine

@CommandLine.Command(name = "schedule", mixinStandardHelpOptions = true, version = ["1.0"])
class Scheduler (
    @Inject
    private val postsRepository: PostsRepository
) {
}