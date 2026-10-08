package adapters.inbound.rest

import application.Output

class RestOutput : Output {
    override fun write(arguments: String): String = arguments
}
