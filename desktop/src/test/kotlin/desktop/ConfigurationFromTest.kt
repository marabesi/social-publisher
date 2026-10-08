package desktop

import application.entities.TwitterCredentials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ConfigurationFromTest {
    @Test
    fun `keeps the file name and the edited fields`() {
        val configuration =
            configurationFrom(
                fileName = "prod",
                storage = "csv",
                timezone = "Europe/Madrid",
                twitter = null,
            )

        assertEquals("prod", configuration.fileName)
        assertEquals("csv", configuration.storage)
        assertEquals("Europe/Madrid", configuration.timezone)
        assertNull(configuration.twitter)
    }

    @Test
    fun `keeps the twitter credentials when at least one field is set`() {
        val configuration =
            configurationFrom(
                fileName = "prod",
                storage = "csv",
                timezone = "UTC",
                twitter = TwitterCredentials(consumerKey = "consumer-key"),
            )

        assertEquals("consumer-key", configuration.twitter?.consumerKey)
    }

    @Test
    fun `drops empty twitter credentials`() {
        val configuration =
            configurationFrom(
                fileName = "prod",
                storage = "csv",
                timezone = "UTC",
                twitter = TwitterCredentials(),
            )

        assertNull(configuration.twitter)
    }
}
