import cli.CliFactory
import cli.Post
import cli.Poster
import cli.Scheduler
import picocli.CommandLine

@CommandLine.Command(
    name = "social",
    description = ["post to any social media"],
    subcommands = [
        Post::class,
        Scheduler::class,
        Poster::class
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