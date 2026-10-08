package adapters.inbound.cli.desktop

import com.google.inject.Inject
import desktop.launchDesktop
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "run", mixinStandardHelpOptions = true)
class DesktopRun
    @Inject
    constructor() : Callable<String> {
        override fun call(): String {
            launchDesktop()
            return ""
        }
    }
