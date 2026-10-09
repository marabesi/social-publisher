package application.search

internal object IdList {
    fun parse(raw: String): List<String> =
        raw
            .split(',', ';', ' ', '\n', '\t')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
