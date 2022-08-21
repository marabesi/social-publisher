package application.scheduler.filters

import java.time.Instant
import java.time.format.DateTimeParseException

class DateTimeValidation(
    private val datetime: String,
) {

    private lateinit var parsedDateTime: Instant

    fun isDateTimeValid(): Boolean {
        return try {
            parsedDateTime = Instant.parse(datetime)
            true
        } catch (_: DateTimeParseException) {
            false
        }
    }

    fun value(): Instant {
        return parsedDateTime
    }
}
