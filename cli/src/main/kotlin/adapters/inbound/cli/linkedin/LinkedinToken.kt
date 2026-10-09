package adapters.inbound.cli.linkedin

import application.Output
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.ConnectLinkedIn
import application.socialnetwork.ExchangeLinkedInAuthorization
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(
    name = "token",
    mixinStandardHelpOptions = true,
    description = ["Exchange the authorization code for an access token and store it"],
)
class LinkedinToken
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
        private val exchange: ExchangeLinkedInAuthorization,
        private val cliOutput: Output,
    ) : Callable<String> {
        @CommandLine.Option(
            names = ["-c", "--code"],
            required = true,
            description = ["Authorization code from the redirect URL"],
        )
        var code: String = ""

        override fun call(): String = ConnectLinkedIn(configurationRepository, exchange, cliOutput).invoke(code)
    }
