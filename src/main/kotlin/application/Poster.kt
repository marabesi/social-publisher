package application

import application.persistence.SchedulerRepository
import application.poster.Executor
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "poster", mixinStandardHelpOptions = true)
class Poster(
    @Inject
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val currentDate: Instant
): Callable<String> {
    @CommandLine.Option(names = ["-p"], description = ["Post Id"])
    var postId: String = ""

    @CommandLine.Option(names = ["-s"], description = ["Social media to post"])
    var socialMedia: String = ""

    @CommandLine.Option(names = ["-r"], description = ["Executes routine that goes over and post posts"])
    var run: Boolean = false

    override fun call(): String {
        if (run) {
            return Executor(schedulerRepository, cliOutput, currentDate).invoke()
        }

        return cliOutput.write("Post 1 set to twitter")
    }
}
