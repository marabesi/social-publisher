import application.Output

class MockedOutput : Output {
    override fun write(arguments: String): String = arguments
}
