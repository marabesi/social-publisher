package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class ComposePostPageTest {
    @Test
    fun `creates a new post from an empty editor`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { composePostPage(store) }

            onNodeWithText("Compose a new post").assertIsDisplayed()
            onNodeWithText("0 / 280").assertIsDisplayed()
            onNode(hasSetTextAction()).performTextReplacement("brand new post")
            onNodeWithText("Create post").performClick()

            assertEquals(1, store.posts().size)
            assertEquals("brand new post", store.posts().first().text)
        }

    @Test
    fun `edits a post with the same editor prepopulated`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop"))
            var finished = false

            setContent {
                composePostPage(
                    store = store,
                    postToEdit = store.posts().first(),
                    onFinish = { finished = true },
                )
            }

            onNodeWithText("Edit post").assertIsDisplayed()
            onNodeWithText("hello desktop").assertIsDisplayed()
            onNode(hasSetTextAction()).performTextReplacement("edited text")
            onNodeWithText("Save").performClick()

            assertTrue(finished)
            assertEquals("edited text", store.posts().first().text)
        }

    @Test
    fun `cancels editing without changing the post`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop"))
            var finished = false

            setContent {
                composePostPage(
                    store = store,
                    postToEdit = store.posts().first(),
                    onFinish = { finished = true },
                )
            }

            onNodeWithText("Cancel").performClick()

            assertTrue(finished)
            assertEquals("hello desktop", store.posts().first().text)
        }

    private fun storeWith(posts: List<String> = emptyList()): SocialPublisherStore {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(posts.map { SocialPosts(text = it) }.toCollection(arrayListOf()))

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
