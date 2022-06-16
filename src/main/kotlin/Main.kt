import adapters.inbound.cli.CliFactory
import adapters.inbound.cli.Configuration
import application.Post
import application.Poster
import application.Scheduler
import picocli.CommandLine

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

fun buildCommandLine(): CommandLine {
    return CommandLine(Application::class.java, CliFactory())
}

fun main(args: Array<String>) {
    buildCommandLine().execute(*args)
}