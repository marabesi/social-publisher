package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalTestApi::class)
class SchedulesPageTest {
    @Test
    fun `uses a datetime picker for the publish date`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { schedulesPage(store) }

            onNodeWithText("Publish date").assertIsDisplayed()
            onNodeWithContentDescription("Pick publish date").performClick()

            onNodeWithText("Ok").assertIsDisplayed()
            onNodeWithText("Cancel").assertIsDisplayed()
        }

    @Test
    fun `schedules a post using the date picked in the dialog`() =
        runComposeUiTest {
            val store = storeWith()
            store.createPost("release notes")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_POST_ID_TAG).performTextInput("1")

            onNodeWithContentDescription("Pick publish date").performClick()
            onAllNodes(
                hasText(",", substring = true) and hasAnyAncestor(hasTestTag(DATE_PICKER_TAG)),
                useUnmergedTree = true,
            )[0].performClick()
            onNodeWithText("Ok").performClick()

            onNodeWithTag(SCHEDULE_CREATE_TAG).performClick()

            assertTrue(store.schedules().isNotEmpty())
        }

    @Test
    fun `selects the social media to schedule for from the dropdown`() =
        runComposeUiTest {
            val store = storeWith()
            store.createPost("release notes")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_POST_ID_TAG).performTextInput("1")
            onNodeWithTag(SCHEDULE_SOCIAL_MEDIA_TAG).performClick()

            onNodeWithTag("$SCHEDULE_SOCIAL_MEDIA_TAG-${SocialMedia.TWITTER.name}").performClick()

            onNodeWithContentDescription("Pick publish date").performClick()
            onAllNodes(
                hasText(",", substring = true) and hasAnyAncestor(hasTestTag(DATE_PICKER_TAG)),
                useUnmergedTree = true,
            )[0].performClick()
            onNodeWithText("Ok").performClick()

            onNodeWithTag(SCHEDULE_CREATE_TAG).performClick()

            assertTrue(store.schedules().isNotEmpty())
            assertEquals(SocialMedia.TWITTER, store.schedules()[0].socialMedia)
        }

    @Test
    fun `filters schedules by fuzzy post text`() =
        runComposeUiTest {
            val store =
                storeWith(
                    posts = listOf("hello desktop", "release notes"),
                    schedules = listOf(0 to SocialMedia.TWITTER, 1 to SocialMedia.TWITTER),
                )

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_SEARCH_TEXT_TAG).performTextInput("hello desk")

            onNodeWithText("Post 1 on", substring = true).assertIsDisplayed()
            onNodeWithText("Post 2 on", substring = true).assertDoesNotExist()
        }

    @Test
    fun `filters schedules by more than one post id`() =
        runComposeUiTest {
            val store =
                storeWith(
                    posts = listOf("first", "second", "third"),
                    schedules =
                        listOf(
                            0 to SocialMedia.TWITTER,
                            1 to SocialMedia.TWITTER,
                            2 to SocialMedia.TWITTER,
                        ),
                )

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_SEARCH_IDS_TAG).performTextInput("1,3")

            onNodeWithText("Post 1 on", substring = true).assertIsDisplayed()
            onNodeWithText("Post 3 on", substring = true).assertIsDisplayed()
            onNodeWithText("Post 2 on", substring = true).assertDoesNotExist()
        }

    @Test
    fun `filters schedules by social network`() =
        runComposeUiTest {
            val store =
                storeWith(
                    posts = listOf("hello desktop", "release notes"),
                    schedules = listOf(0 to SocialMedia.LINKEDIN, 1 to SocialMedia.TWITTER),
                )

            setContent { schedulesPage(store) }

            onNodeWithText(ALL_SOCIAL_MEDIA_LABEL).performClick()
            onNodeWithTag("$SCHEDULE_SEARCH_SOCIAL_MEDIA_TAG-${SocialMedia.LINKEDIN.name}").performClick()

            onNodeWithText("Post 1 on", substring = true).assertIsDisplayed()
            onNodeWithText("Post 2 on", substring = true).assertDoesNotExist()
        }

    @Test
    fun `shows a message when no schedule matches the search`() =
        runComposeUiTest {
            val store =
                storeWith(
                    posts = listOf("hello desktop"),
                    schedules = listOf(0 to SocialMedia.TWITTER),
                )

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_SEARCH_TEXT_TAG).performTextInput("zzz")

            onNodeWithText("No schedules match your search").assertIsDisplayed()
        }

    @Test
    fun `picks a random post for the default day`() =
        runComposeUiTest {
            val store = storeWith(currentDate = { Instant.parse("2020-01-01T00:00:00Z") })
            store.createPost("release notes")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_RANDOM_TAG).performClick()

            assertTrue(store.schedules().isNotEmpty())
            onNodeWithText("Post 1 has been randomly scheduled for", substring = true).assertIsDisplayed()
        }

    @Test
    fun `picks a random post for the day of the app clock`() =
        runComposeUiTest {
            val store = storeWith(currentDate = { Instant.parse("2026-10-05T10:00:00Z") })
            store.createPost("release notes")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_RANDOM_TAG).performClick()

            assertTrue(store.schedules().isNotEmpty())
            val publishDate = store.schedules()[0].publishDate
            assertEquals("2026-10-05", publishDate.atZone(ZoneOffset.UTC).toLocalDate().toString())
        }

    @Test
    fun `sorts schedules by publish date ascending`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("first", "second", "third"))
            store.schedule("1", "2022-10-03T09:00:00Z")
            store.schedule("2", "2022-10-01T09:00:00Z")
            store.schedule("3", "2022-10-02T09:00:00Z")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_SORT_TAG).performClick()
            onNodeWithTag("$SCHEDULE_SORT_TAG-ASC").performClick()

            val items = onAllNodesWithText(". Post ", substring = true)
            items[0].assertTextContains("Post 2 on", substring = true)
            items[1].assertTextContains("Post 3 on", substring = true)
            items[2].assertTextContains("Post 1 on", substring = true)
        }

    @Test
    fun `sorts schedules by publish date descending`() =
        runComposeUiTest {
            val store = storeWith(posts = listOf("first", "second", "third"))
            store.schedule("1", "2022-10-03T09:00:00Z")
            store.schedule("2", "2022-10-01T09:00:00Z")
            store.schedule("3", "2022-10-02T09:00:00Z")

            setContent { schedulesPage(store) }

            onNodeWithTag(SCHEDULE_SORT_TAG).performClick()
            onNodeWithTag("$SCHEDULE_SORT_TAG-DESC").performClick()

            val items = onAllNodesWithText(". Post ", substring = true)
            items[0].assertTextContains("Post 1 on", substring = true)
            items[1].assertTextContains("Post 3 on", substring = true)
            items[2].assertTextContains("Post 2 on", substring = true)
        }

    private fun storeWith(
        posts: List<String> = emptyList(),
        schedules: List<Pair<Int, SocialMedia>> = emptyList(),
        currentDate: () -> Instant = { Instant.now() },
    ): SocialPublisherStore {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(posts.map { SocialPosts(text = it) }.toCollection(arrayListOf()))

        val schedulerRepository = InMemorySchedulerRepository()
        schedules.forEachIndexed { position, (index, media) ->
            schedulerRepository.save(
                ScheduledItem(
                    post = postsRepository.findAll()[index],
                    publishDate = Instant.parse("2022-10-02T09:00:00Z").plusSeconds(position.toLong()),
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
            currentDate = currentDate,
        )
    }
}
