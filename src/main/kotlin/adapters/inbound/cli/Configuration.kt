package adapters.inbound.cli

import application.Output
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import com.google.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import picocli.CommandLine
import java.util.concurrent.Callable
import application.configuration.List

@CommandLine.Command(name = "configuration", mixinStandardHelpOptions = true, version = ["1.0"])
class Configuration(
    @Inject
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
): Callable<String> {

    @CommandLine.Option(names = ["-c"], description = ["JSON with the desired configuration"])
    var configuration: String = ""

    @CommandLine.Option(names = ["-l"], description = ["List the stored configuration"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            return List(cliOutput, configurationRepository).invoke()
        }

        if (configuration.isEmpty()) {
            return cliOutput.write("Missing required fields")
        }
        val data = Json.decodeFromString<SocialConfiguration>(configuration)

        if (data.storage.isEmpty()) {
            data.storage = "csv"
        }

        configurationRepository.save(data)
        return cliOutput.write("Configuration has been stored")
    }
}