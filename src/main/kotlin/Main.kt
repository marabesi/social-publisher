import adapters.inbound.cli.CliFactory
import adapters.inbound.cli.Configuration
import adapters.inbound.cli.Post
import application.Poster
import adapters.inbound.cli.Scheduler
import picocli.CommandLine
import java.time.Instant

@CommandLine.Command(
    name = "social",
    description = ["post to any social media"],
    subcommands = [
        Post::class,
        Scheduler::class,
        Poster::class,
        Configuration::class,
    ]
)
class Application {
}

fun buildCommandLine(currentTime: Instant = Instant.now()): CommandLine {
    return CommandLine(Application::class.java, CliFactory(currentTime))
}

fun main(args: Array<String>) {
    buildCommandLine().execute(*args)
}