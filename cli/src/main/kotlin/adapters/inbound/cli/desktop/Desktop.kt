package adapters.inbound.cli.desktop

import application.Messages
import application.Output
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "desktop",
    mixinStandardHelpOptions = true,
    subcommands = [DesktopRun::class],
)
class Desktop
    @Inject
    constructor(
        private val cliOutput: Output,
    ) : Callable<String> {
        override fun call(): String = cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
