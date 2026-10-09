package application.scheduler.filters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class DateTimeValidationTest {
    @Test
    fun `should parse an instant that has an explicit utc offset`() {
        val validation = DateTimeValidation("2022-10-02T09:00:00Z", ZoneId.of("Europe/Madrid"))

        assertTrue(validation.isDateTimeValid())
        assertEquals(Instant.parse("2022-10-02T09:00:00Z"), validation.value())
    }

    @Test
    fun `should parse an instant that has an explicit offset`() {
        val validation = DateTimeValidation("2022-10-02T09:00:00+02:00", ZoneOffset.UTC)

        assertTrue(validation.isDateTimeValid())
        assertEquals(Instant.parse("2022-10-02T07:00:00Z"), validation.value())
    }

    @Test
    fun `should interpret a local date time using the given timezone`() {
        val validation = DateTimeValidation("2022-10-02T09:00:00", ZoneId.of("Europe/Madrid"))

        assertTrue(validation.isDateTimeValid())
        assertEquals(Instant.parse("2022-10-02T07:00:00Z"), validation.value())
    }

    @Test
    fun `should interpret a local date time using utc by default`() {
        val validation = DateTimeValidation("2022-10-02T09:00:00")

        assertTrue(validation.isDateTimeValid())
        assertEquals(Instant.parse("2022-10-02T09:00:00Z"), validation.value())
    }

    @Test
    fun `should reject an invalid date time`() {
        assertFalse(DateTimeValidation("2022", ZoneOffset.UTC).isDateTimeValid())
    }
}
