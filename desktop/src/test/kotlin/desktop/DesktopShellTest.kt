package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class DesktopShellTest {
    @Test
    fun `navigates through every page from the drawer`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { desktopAppShell(store) }

            onNodeWithText("What do you want to do?").assertIsDisplayed()
            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()
            onNodeWithText("hello desktop").assertIsDisplayed()

            onNodeWithContentDescription("Menu").performClick()
            onNodeWithText("Schedules").performClick()
            onNodeWithText("No schedules yet").assertIsDisplayed()

            onNodeWithContentDescription("Menu").performClick()
            onNodeWithText("Compose").performClick()
            onNodeWithText("Create post").assertIsDisplayed()

            onNodeWithContentDescription("Menu").performClick()
            onNodeWithText("Poster").performClick()
            onNodeWithText("Run every").assertIsDisplayed()
            onNodeWithText("The poster is disabled.").assertIsDisplayed()

            onNodeWithContentDescription("Menu").performClick()
            onNodeWithText("Configuration").performClick()
            onNodeWithText("Store configuration").assertIsDisplayed()
        }

    @Test
    fun `opens the shared editor prepopulated when editing a post`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { desktopAppShell(store) }

            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()
            onNodeWithText("Edit").performClick()

            onNodeWithText("Edit post").assertIsDisplayed()
            onNodeWithText("hello desktop").assertIsDisplayed()
            onNodeWithText("Save").assertIsDisplayed()
        }

    private fun storeWith(): SocialPublisherStore {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop")))

        return SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = InMemorySchedulerRepository(),
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(timezone = "UTC")) },
            output = MockedOutput(),
            socialNetworks = MockedSocialThirdParty(),
        )
    }
}
