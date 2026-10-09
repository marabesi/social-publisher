package desktop

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

@OptIn(ExperimentalTestApi::class)
class DateTimePickerTest {
    @Test
    fun `converts a date and time into an ISO instant`() {
        val millis = Instant.parse("2026-10-02T00:00:00Z").toEpochMilli()

        assertEquals("2026-10-02T09:30:00Z", toIsoInstant(millis, 9, 30))
        assertEquals("2026-10-02T00:00:00Z", toIsoInstant(millis, 0, 0))
        assertEquals("2026-10-02T23:59:00Z", toIsoInstant(millis, 23, 59))
    }

    @Test
    fun `opens a picker dialog instead of accepting free text`() =
        runComposeUiTest {
            setContent {
                dateTimePicker(label = "Publish date", value = "", onValueChange = {})
            }

            onNodeWithContentDescription("Pick date").performClick()

            onNodeWithText("Ok").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
        }

    @Test
    fun `lays the clock beside the calendar without overlapping`() =
        runComposeUiTest {
            setContent {
                dateTimePicker(label = "Publish date", value = "", onValueChange = {})
            }

            onNodeWithContentDescription("Pick date").performClick()

            val dialog = onNodeWithTag(DATE_TIME_DIALOG_TAG).getUnclippedBoundsInRoot()
            val calendar = onNodeWithTag(DATE_PICKER_TAG).getUnclippedBoundsInRoot()
            val clock = onNodeWithTag(TIME_PICKER_TAG).getUnclippedBoundsInRoot()

            assertTrue(clock.left >= calendar.right)
            assertTrue(clock.right <= dialog.right)
            assertTrue(clock.right - clock.left >= 200.dp)
        }
}
