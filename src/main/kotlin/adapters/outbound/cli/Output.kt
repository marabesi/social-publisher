package adapters.outbound.cli

interface Output {

    fun write(arguments: String): String
}
