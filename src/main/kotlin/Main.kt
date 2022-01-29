import picocli.CommandLine

@CommandLine.Command(
    name = "social",
    description = ["post to any social media"],
    subcommands = [
        Post::class,
        Scheduler::class
    ]
)
class Application {
}

fun buildCommandLine(): CommandLine {
    return CommandLine(Application::class.java, MyFactory())
}

fun main(args: Array<String>) {
    buildCommandLine().execute(*args)
}