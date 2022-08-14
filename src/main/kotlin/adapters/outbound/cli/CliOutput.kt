package adapters.outbound.cli

import application.Output

class CliOutput: Output {

    override fun write(arguments: String): String {
        println(arguments)
        return arguments
    }
}
