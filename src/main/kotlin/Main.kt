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

fun main(args: Array<String>) {
    CommandLine(Application())
        .execute(*args)
}