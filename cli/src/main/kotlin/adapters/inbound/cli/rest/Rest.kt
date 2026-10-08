package adapters.inbound.cli.rest

import application.Messages
import application.Output
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "rest",
    mixinStandardHelpOptions = true,
    subcommands = [RestServe::class],
)
class Rest
    @Inject
    constructor(
        private val cliOutput: Output,
    ) : Callable<String> {
        override fun call(): String = cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
