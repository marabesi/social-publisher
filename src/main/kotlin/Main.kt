import adapters.inbound.cli.CliFactory
import adapters.inbound.cli.Configuration
import adapters.inbound.cli.Post
import adapters.inbound.cli.Poster
import adapters.inbound.cli.scheduler.Scheduler
import adapters.outbound.cli.CliOutput
import application.Output
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
class Main

fun buildCommandLine(
    currentTime: Instant = Instant.now(),
    output: Output = CliOutput()
): CommandLine {
    return CommandLine(Main::class.java, CliFactory(currentTime, output))
}

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    buildCommandLine().execute(*args)
}
