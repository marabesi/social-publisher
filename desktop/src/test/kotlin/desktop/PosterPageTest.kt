package desktop

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

@OptIn(ExperimentalTestApi::class)
class PosterPageTest {
    @Test
    fun `shows the disabled state and the default cadence`() =
        runComposeUiTest {
            val state = PosterState(runPoster = { "Post 1 sent to twitter" })

            setContent { posterPage(state) }

            onNodeWithText("The poster is disabled.").assertIsDisplayed()
            onNodeWithText("Every minute").assertIsDisplayed()
        }

    @Test
    fun `shows the next run time when enabled`() =
        runComposeUiTest {
            val fixed = Instant.parse("2026-10-02T09:00:00Z")
            val state = PosterState(runPoster = { "run" }, clock = { fixed })

            setContent { posterPage(state) }

            onNodeWithTag("poster-enabled").performClick()

            onNodeWithText("Next run: ", substring = true).assertIsDisplayed()
        }

    @Test
    fun `changes the cadence from the menu`() =
        runComposeUiTest {
            val state = PosterState(runPoster = { "run" })

            setContent { posterPage(state) }

            onNodeWithText("Every minute").performClick()
            onNodeWithText("Every 5 minutes").performClick()

            assertEquals(5, state.cadenceMinutes)
        }

    @Test
    fun `runs the poster on demand`() =
        runComposeUiTest {
            val state = PosterState(runPoster = { "Post 1 sent to twitter" })

            setContent { posterPage(state) }

            onNodeWithText("Run now").performClick()

            onNodeWithText("Post 1 sent to twitter").assertIsDisplayed()
        }
}
