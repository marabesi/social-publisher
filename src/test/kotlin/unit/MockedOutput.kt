package unit

import adapters.outbound.Output

class MockedOutput : Output {
    override fun write(arguments: String): String {
        return arguments
    }
}
