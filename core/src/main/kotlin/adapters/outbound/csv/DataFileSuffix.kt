package adapters.outbound.csv

import application.persistence.configuration.ConfigurationRepository

class DataFileSuffix(
    private val configurationRepository: ConfigurationRepository?,
    private val fallback: String = DEFAULT_FALLBACK,
) {
    fun value(): String {
        val repository = configurationRepository ?: return fallback

        val fileName = runCatching { repository.find().fileName }.getOrNull().orEmpty()
        return fileName.ifBlank { fallback }
    }

    companion object {
        const val DEFAULT_FALLBACK = "production"
    }
}
