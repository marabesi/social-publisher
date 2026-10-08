package adapters.outbound.csv

class StorePath(
    private val environment: (String) -> String? = { System.getenv(it) },
) {
    fun directory(): String =
        environment(ENVIRONMENT_VARIABLE)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_DIRECTORY

    fun file(fileName: String): String = "${directory()}/$fileName"

    companion object {
        private const val DEFAULT_DIRECTORY = "data"
        private const val ENVIRONMENT_VARIABLE = "SOCIAL_STORE_PATH"

        val DEFAULT: StorePath = StorePath()
    }
}
