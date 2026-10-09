package application

import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZoneOffset

object Timezone {
    const val DEFAULT = "UTC"

    fun configured(repository: ConfigurationRepository): String =
        try {
            repository.find().timezone.ifBlank { DEFAULT }
        } catch (_: MissingConfiguration) {
            DEFAULT
        }

    fun zoneId(name: String): ZoneId =
        if (name.isBlank()) {
            ZoneOffset.UTC
        } else {
            try {
                ZoneId.of(name)
            } catch (_: DateTimeException) {
                ZoneOffset.UTC
            }
        }

    fun zoneId(repository: ConfigurationRepository): ZoneId = zoneId(configured(repository))
}
