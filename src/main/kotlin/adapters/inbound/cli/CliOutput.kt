package adapters.inbound.cli

import adapters.outbound.cli.Output

class CliOutput: Output {

    override fun write(arguments: String): String {
        print(arguments)
        return arguments
    }
}