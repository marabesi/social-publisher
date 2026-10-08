package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import application.entities.SocialConfiguration
import application.entities.SocialPosts
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class PostsPageTest {
    @Test
    fun `shows the stored posts`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post"))

            setContent { postsPage(store) }

            onNodeWithText("1. hello desktop").assertIsDisplayed()
            onNodeWithText("2. second post").assertIsDisplayed()
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
        )
    }
}
