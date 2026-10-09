package adapters.inbound.cli

import application.Messages
import application.Output
import application.entities.SocialMedia
import application.persistence.SchedulerRepository
import application.poster.Executor
import application.socialnetwork.SocialThirdPartyProvider
import com.google.inject.Inject
import picocli.CommandLine
import java.time.Instant
import java.util.concurrent.Callable

@CommandLine.Command(name = "poster", mixinStandardHelpOptions = true)
class Poster
    @Inject
    constructor(
        private val schedulerRepository: SchedulerRepository,
        private val cliOutput: Output,
        private val currentDate: Instant,
        private val socialNetworks: SocialThirdPartyProvider,
    ) : Callable<String> {
        @CommandLine.Option(names = ["-p"], description = ["Post Id"])
        var postId: String = ""

        @CommandLine.Option(names = ["-s"], description = ["Social media to post"])
        var socialMedia: String = ""

        @CommandLine.Option(names = ["-r"], description = ["Executes routine that goes over and post posts"])
        var run: Boolean? = false

        override fun call(): String {
            if (run == true) {
                return Executor(
                    schedulerRepository,
                    cliOutput,
                    currentDate,
                    socialNetworks,
                ).invoke()
            }

            if (postId.isEmpty()) {
                return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
            }

            val target = socialMedia.ifBlank { SocialMedia.TWITTER.displayName }.lowercase()

            return cliOutput.write("Post $postId set to $target")
        }
    }
