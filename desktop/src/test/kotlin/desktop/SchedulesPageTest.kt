package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class SchedulesPageTest {
    @Test
    fun `uses a datetime picker for the publish date`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { schedulesPage(store) }

            onNodeWithText("Publish date").assertIsDisplayed()
            onNodeWithContentDescription("Pick date").performClick()

            onNodeWithText("Ok").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
        }

    @Test
    fun `schedules a post using the date picked in the dialog`() =
        runComposeUiTest {
            val store = storeWith()
            store.createPost("release notes")

            setContent { schedulesPage(store) }

            onNode(hasSetTextAction()).performTextInput("1")

            onNodeWithContentDescription("Pick date").performClick()
            onAllNodes(
                hasText(",", substring = true) and hasAnyAncestor(hasTestTag(DATE_PICKER_TAG)),
                useUnmergedTree = true,
            )[0].performClick()
            onNodeWithText("Ok").performClick()

            onNodeWithText("Schedule").performClick()

            assertTrue(store.schedules().isNotEmpty())
        }

    private fun storeWith(): SocialPublisherStore =
        SocialPublisherStore(
            postsRepository = InMemoryPostRepository(),
            schedulerRepository = InMemorySchedulerRepository(),
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(timezone = "UTC")) },
            output = MockedOutput(),
            twitterClient = MockedSocialThirdParty(),
        )
}
