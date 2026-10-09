package adapters.inbound.cli.linkedin

import application.Output
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.AuthorizationUrl
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "connect",
    mixinStandardHelpOptions = true,
    description = ["Print the LinkedIn authorization URL to obtain an access token"],
)
class LinkedinConnect
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
        private val cliOutput: Output,
    ) : Callable<String> {
        @CommandLine.Option(
            names = ["--state"],
            description = ["Opaque value echoed back by LinkedIn to protect against CSRF"],
            defaultValue = AuthorizationUrl.DEFAULT_STATE,
        )
        var state: String = AuthorizationUrl.DEFAULT_STATE

        override fun call(): String = AuthorizationUrl(configurationRepository, cliOutput).invoke(state)
    }
