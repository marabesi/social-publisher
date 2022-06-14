package cli

import com.google.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import persistence.ConfigurationRepository
import picocli.CommandLine
import socialPosts.SocialConfiguration
import java.util.concurrent.Callable

@CommandLine.Command(name = "configuration", mixinStandardHelpOptions = true, version = ["1.0"])
class Configuration(
    @Inject
    private val cliOutput: Output,
    private val configurationRepository: ConfigurationRepository
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Desired file name to store the configuration"])
    var path: String = ""

    @CommandLine.Option(names = ["-c"], description = ["JSON with the desired configuration"])
    lateinit var configuration: String

    @CommandLine.Option(names = ["-l"], description = ["List the stored configuration"])
    var list: Boolean = false

    override fun call(): String {
        if (list) {
            val data: SocialConfiguration = configurationRepository.find(path)
            return cliOutput.write(Json.encodeToString(data))
        }

        if (path.isNotEmpty()) {
            configurationRepository.save(path)
            return cliOutput.write("Configuration has been stored")
        }
        return cliOutput.write("Missing required fields")
    }
}