package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.persistence.PostsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.CountDownLatch

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

            waitUntil { store.posts().size == 1 }
            onNodeWithText("second post").assertDoesNotExist()
            onNodeWithText("hello desktop").assertIsDisplayed()
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

    @Test
    fun `filters posts by fuzzy text`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post"))

            setContent { postsPage(store) }

            onNodeWithTag(POSTS_SEARCH_TEXT_TAG).performTextInput("hello desk")

            onNodeWithText("hello desktop").assertIsDisplayed()
            onNodeWithText("second post").assertDoesNotExist()
        }

    @Test
    fun `filters posts by more than one id`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop", "second post", "release notes"))

            setContent { postsPage(store) }

            onNodeWithTag(POSTS_SEARCH_IDS_TAG).performTextInput("1,3")

            onNodeWithText("hello desktop").assertIsDisplayed()
            onNodeWithText("release notes").assertIsDisplayed()
            onNodeWithText("second post").assertDoesNotExist()
        }

    @Test
    fun `filters posts by social network`() =
        runComposeUiTest {
            val store =
                storeWith(
                    posts = listOf("hello desktop", "second post"),
                    schedules = listOf(1 to SocialMedia.LINKEDIN),
                )

            setContent { postsPage(store) }

            onNodeWithText(ALL_SOCIAL_MEDIA_LABEL).performClick()
            onNodeWithTag("$POSTS_SOCIAL_MEDIA_TAG-${SocialMedia.LINKEDIN.name}").performClick()

            onNodeWithText("second post").assertIsDisplayed()
            onNodeWithText("hello desktop").assertDoesNotExist()
        }

    @Test
    fun `shows a message when no post matches the search`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("hello desktop"))

            setContent { postsPage(store) }

            onNodeWithTag(POSTS_SEARCH_TEXT_TAG).performTextInput("zzz")

            onNodeWithText("No posts match your search").assertIsDisplayed()
        }

    @Test
    fun `sorts posts by id ascending`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("first", "second", "third"))

            setContent { postsPage(store) }

            onNodeWithTag(POSTS_SORT_TAG).performClick()
            onNodeWithTag("$POSTS_SORT_TAG-ASC").performClick()

            val first = onNodeWithText("first").fetchSemanticsNode().boundsInRoot.top
            val second = onNodeWithText("second").fetchSemanticsNode().boundsInRoot.top
            val third = onNodeWithText("third").fetchSemanticsNode().boundsInRoot.top
            assertTrue(first < second)
            assertTrue(second < third)
        }

    @Test
    fun `sorts posts by id descending`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("first", "second", "third"))

            setContent { postsPage(store) }

            onNodeWithTag(POSTS_SORT_TAG).performClick()
            onNodeWithTag("$POSTS_SORT_TAG-DESC").performClick()

            val first = onNodeWithText("first").fetchSemanticsNode().boundsInRoot.top
            val second = onNodeWithText("second").fetchSemanticsNode().boundsInRoot.top
            val third = onNodeWithText("third").fetchSemanticsNode().boundsInRoot.top
            assertTrue(third < second)
            assertTrue(second < first)
        }

    @Test
    fun `shows a loading indicator while refreshing the posts`() =
        runComposeUiTest {
            val postsRepository = BlockingFindAllPostsRepository()
            val store = storeWith(posts = listOf("hello desktop"), postsRepository = postsRepository)

            setContent { postsPage(store) }

            onNodeWithText("hello desktop").assertIsDisplayed()

            postsRepository.block = true
            onNodeWithText("Refresh").performClick()

            waitUntil {
                onAllNodesWithTag(LOADING_INDICATOR_TAG).fetchSemanticsNodes().isNotEmpty()
            }
            onNodeWithTag(LOADING_INDICATOR_TAG).assertIsDisplayed()

            postsRepository.release()

            waitUntil {
                onAllNodesWithTag(LOADING_INDICATOR_TAG).fetchSemanticsNodes().isEmpty()
            }
            onNodeWithText("hello desktop").assertIsDisplayed()
        }

    private fun storeWith(
        posts: List<String>,
        schedules: List<Pair<Int, SocialMedia>> = emptyList(),
        postsRepository: PostsRepository = InMemoryPostRepository(),
    ): SocialPublisherStore {
        postsRepository.save(posts.map { SocialPosts(text = it) }.toCollection(arrayListOf()))

        val schedulerRepository = InMemorySchedulerRepository()
        schedules.forEach { (index, media) ->
            schedulerRepository.save(
                ScheduledItem(
                    post = postsRepository.findAll()[index],
                    publishDate = Instant.EPOCH,
                    socialMedia = media,
                ),
            )
        }

        return SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = schedulerRepository,
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(timezone = "UTC")) },
            output = MockedOutput(),
            socialNetworks = MockedSocialThirdParty(),
        )
    }
}

private class BlockingFindAllPostsRepository(
    private val delegate: PostsRepository = InMemoryPostRepository(),
) : PostsRepository by delegate {
    @Volatile
    var block: Boolean = false

    private val gate = CountDownLatch(1)

    override fun findAll(): ArrayList<SocialPosts> {
        if (block) {
            gate.await()
        }
        return delegate.findAll()
    }

    fun release() {
        gate.countDown()
    }
}
