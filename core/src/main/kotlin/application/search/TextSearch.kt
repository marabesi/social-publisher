package application.search

internal object TextSearch {
    private val WHITESPACE = Regex("\\s+")

    fun matches(
        query: String,
        text: String,
    ): Boolean {
        val tokens =
            query
                .lowercase()
                .trim()
                .split(WHITESPACE)
                .filter { it.isNotEmpty() }
        if (tokens.isEmpty()) {
            return true
        }

        val haystack = text.lowercase()
        return tokens.all { startsAWord(haystack, it) }
    }

    private fun startsAWord(
        haystack: String,
        token: String,
    ): Boolean {
        var index = haystack.indexOf(token)
        while (index >= 0) {
            if (index == 0 || !haystack[index - 1].isLetterOrDigit()) {
                return true
            }
            index = haystack.indexOf(token, startIndex = index + 1)
        }
        return false
    }
}
