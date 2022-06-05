package cli

class CliOutput: Output {

    override fun write(arguments: String): String {
        print(arguments)
        return arguments
    }
}