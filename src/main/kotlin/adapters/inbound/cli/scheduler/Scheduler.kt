package adapters.inbound.cli.scheduler

import application.Messages
import application.Output
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "scheduler", mixinStandardHelpOptions = true,
    subcommands = [
        SchedulerList::class,
        SchedulerCreate::class,
        SchedulerDelete::class
    ]
)
class Scheduler(
    private val cliOutput: Output,
) : Callable<String> {

    override fun call(): String {
        return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
}
