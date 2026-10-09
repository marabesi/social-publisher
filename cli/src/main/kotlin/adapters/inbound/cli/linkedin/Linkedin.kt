package adapters.inbound.cli.linkedin

import application.Messages
import application.Output
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "linkedin",
    mixinStandardHelpOptions = true,
    description = ["Connect a LinkedIn account"],
    subcommands = [
        LinkedinConnect::class,
        LinkedinToken::class,
    ],
)
class Linkedin
    @Inject
    constructor(
        private val cliOutput: Output,
    ) : Callable<String> {
        override fun call(): String = cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
