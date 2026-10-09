package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import application.persistence.PostsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class ErrorHandlingTest {
    @Test
    fun `shows the error and its cause instead of crashing when removing a post fails`() =
        runComposeUiTest {
            val postsRepository = FailingDeletePostsRepository()
            postsRepository.save(arrayListOf(SocialPosts(text = "hello desktop")))

            setContent { desktopAppShell(storeWith(postsRepository)) }

            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()
            onNodeWithText("Remove").performClick()

            onNodeWithText("Something went wrong").assertIsDisplayed()
            onNodeWithText("Could not remove the post").assertIsDisplayed()
            onNodeWithText("Cause: The store file is locked").assertIsDisplayed()
        }

    @Test
    fun `shows the error instead of crashing when posts cannot be loaded`() =
        runComposeUiTest {
            setContent { desktopAppShell(storeWith(FailingFindAllPostsRepository())) }

            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()

            onNodeWithText("Something went wrong").assertIsDisplayed()
            onNodeWithText("Could not read the posts").assertIsDisplayed()
        }

    @Test
    fun `dismisses the error dialog and keeps the app running`() =
        runComposeUiTest {
            setContent { desktopAppShell(storeWith(FailingFindAllPostsRepository())) }

            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()
            onNodeWithText("Dismiss").performClick()

            onNodeWithText("Something went wrong").assertDoesNotExist()
            onNodeWithText("No posts yet").assertIsDisplayed()
        }

    @Test
    fun `reports a poster failure without crashing`() =
        runComposeUiTest {
            var captured: Throwable? = null
            val state =
                PosterState(
                    runPoster = {
                        throw IllegalStateException("Twitter is unavailable", RuntimeException("connection refused"))
                    },
                )

            setContent {
                CompositionLocalProvider(LocalErrorReporter provides ErrorReporter { captured = it }) {
                    posterPage(state)
                }
            }

            onNodeWithText("Run now").performClick()

            assertEquals("Twitter is unavailable", captured?.message)
            assertEquals("connection refused", captured?.cause?.message)
        }

    private fun storeWith(postsRepository: PostsRepository): SocialPublisherStore =
        SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = InMemorySchedulerRepository(),
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(timezone = "UTC")) },
            output = MockedOutput(),
            socialNetworks = MockedSocialThirdParty(),
        )
}

private class FailingDeletePostsRepository : PostsRepository by InMemoryPostRepository() {
    override fun deleteById(postId: String): SocialPosts? =
        throw IllegalStateException(
            "Could not remove the post",
            RuntimeException("The store file is locked"),
        )
}

private class FailingFindAllPostsRepository : PostsRepository by InMemoryPostRepository() {
    override fun findAll(): ArrayList<SocialPosts> = throw IllegalStateException("Could not read the posts")
}
