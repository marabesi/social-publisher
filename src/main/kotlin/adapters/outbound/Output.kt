package adapters.outbound

interface Output {

    fun write(arguments: String): String
}
