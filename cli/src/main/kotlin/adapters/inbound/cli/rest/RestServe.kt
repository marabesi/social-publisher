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
        lateinit var arguments: List<String>

        override fun call(): String {
            serveRest(arguments.toTypedArray())
            return ""
        }
    }
