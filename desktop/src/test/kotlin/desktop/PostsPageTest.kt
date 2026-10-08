package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class PostsPageTest {
    @Test
    fun `shows the stored posts`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post"))

            setContent { postsPage(store) }

            onNodeWithText("Id").assertIsDisplayed()
            onNodeWithText("Text").assertIsDisplayed()
            onNodeWithText("1").assertIsDisplayed()
            onNodeWithText("hello desktop").assertIsDisplayed()
            onNodeWithText("2").assertIsDisplayed()
            onNodeWithText("second post").assertIsDisplayed()
        }

    @Test
    fun `shows edit and remove actions for every post`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post"))

            setContent { postsPage(store) }

            onNodeWithText("Actions").assertIsDisplayed()
            onAllNodesWithText("Edit").assertCountEquals(2)
            onAllNodesWithText("Remove").assertCountEquals(2)
        }

    @Test
    fun `removes a post when the remove button is clicked`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post"))

            setContent { postsPage(store) }

            onAllNodesWithText("Remove")[0].performClick()

            onNodeWithText("hello desktop").assertDoesNotExist()
            onNodeWithText("second post").assertIsDisplayed()
        }

    @Test
    fun `delegates editing of the selected post`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop"))
            var edited: SocialPosts? = null

            setContent { postsPage(store, onEdit = { edited = it }) }

            onNodeWithText("Edit").performClick()

            assertEquals("1", edited?.id)
            assertEquals("hello desktop", edited?.text)
        }

    @Test
    fun `shows an empty state when there are no posts`() =
        runComposeUiTest {
            val store = storeWith(posts = emptyList())

            setContent { postsPage(store) }

            onNodeWithText("No posts yet").assertIsDisplayed()
        }

    private fun storeWith(posts: List<String>): SocialPublisherStore {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(posts.map { SocialPosts(text = it) }.toCollection(arrayListOf()))

        return SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = InMemorySchedulerRepository(),
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(timezone = "UTC")) },
            output = MockedOutput(),
            twitterClient = MockedSocialThirdParty(),
        )
    }
}
