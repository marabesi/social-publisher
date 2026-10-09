package adapters.inbound.cli.rest

import com.google.inject.Inject
import picocli.CommandLine
import restapi.serveRest
import java.util.concurrent.Callable

@CommandLine.Command(name = "serve", mixinStandardHelpOptions = true)
class RestServe
    @Inject
    constructor() : Callable<String> {
        @CommandLine.Unmatched
        var arguments: MutableList<String> = mutableListOf()

        internal var launcher: (Array<String>) -> Unit = ::serveRest

        override fun call(): String {
            launcher(arguments.toTypedArray())
            return ""
        }
    }
