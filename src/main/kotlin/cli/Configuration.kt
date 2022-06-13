package cli

import com.google.inject.Inject
import picocli.CommandLine
import java.util.concurrent.Callable

@CommandLine.Command(name = "configuration", mixinStandardHelpOptions = true, version = ["1.0"])
class Configuration(
    @Inject
    private val cliOutput: Output
): Callable<String> {

    @CommandLine.Option(names = ["-p"], description = ["Path"])
    var path: String = ""

    @CommandLine.Option(names = ["-c"], description = ["JSON with the desired configuration"])
    lateinit var configuration: String

    override fun call(): String {
        if (path.isNotEmpty()) {
            return cliOutput.write("Configuration has been stored")
        }
        return cliOutput.write("Missing required fields")
    }
}