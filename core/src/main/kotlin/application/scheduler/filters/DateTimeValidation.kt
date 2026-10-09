package application.scheduler.filters

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeParseException

class DateTimeValidation(
    private val datetime: String,
    private val zoneId: ZoneId = ZoneOffset.UTC,
) {
    private lateinit var parsedDateTime: Instant

    fun isDateTimeValid(): Boolean =
        try {
            parsedDateTime = parse()
            true
        } catch (_: DateTimeParseException) {
            false
        }

    fun value(): Instant = parsedDateTime

    private fun parse(): Instant =
        try {
            Instant.parse(datetime)
        } catch (_: DateTimeParseException) {
            LocalDateTime.parse(datetime).atZone(zoneId).toInstant()
        }
}
