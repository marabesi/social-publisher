package adapters.inbound.cli

import application.Output
import application.configuration.Create
import application.configuration.List
import application.persistence.configuration.ConfigurationRepository
import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "configuration", mixinStandardHelpOptions = true)
class Configuration(
    @Inject
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
): Callable<String> {

    @CommandLine.Option(names = ["-c"], description = ["JSON with the desired configuration"])
    var jsonConfiguration: String = ""

    @CommandLine.Option(names = ["-l"], description = ["List the stored configuration"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            return List(cliOutput, configurationRepository).invoke()
        }

        return Create(cliOutput, configurationRepository).invoke(jsonConfiguration)
    }
}
