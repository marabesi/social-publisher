package unit

import adapters.outbound.cli.Output

class MockedOutput : Output {
    override fun write(arguments: String): String {
        return arguments
    }
}
