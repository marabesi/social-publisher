package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
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
