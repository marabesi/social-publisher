package adapters.inbound.cli

import adapters.outbound.cli.Output
import com.google.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import adapters.outbound.persistence.ConfigurationRepository
import picocli.CommandLine
import socialPosts.SocialConfiguration
import java.util.concurrent.Callable

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
            return try {
                val data: SocialConfiguration = configurationRepository.find()
                cliOutput.write(Json.encodeToString(data))
            } catch (error: MissingConfiguration) {
                cliOutput.write(error.message!!)
            }
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