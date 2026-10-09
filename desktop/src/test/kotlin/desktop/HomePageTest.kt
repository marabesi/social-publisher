package desktop

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class HomePageTest {
    @Test
    fun `shows a button for every destination page`() =
        runComposeUiTest {
            setContent { homePage(onNavigate = {}) }

            AppPage.entries.filter { it != AppPage.HOME }.forEach { page ->
                onNodeWithText(page.title).assertIsDisplayed()
            }
        }

    @Test
    fun `navigates to the page chosen from a button`() =
        runComposeUiTest {
            var navigatedTo: AppPage? = null

            setContent { homePage(onNavigate = { navigatedTo = it }) }

            onNodeWithTag(homeNavigationTag(AppPage.SCHEDULES)).performClick()

            assertEquals(AppPage.SCHEDULES, navigatedTo)
        }
}
