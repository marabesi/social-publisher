package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SkikoComposeUiTest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import application.entities.ScheduledItem
import application.entities.SocialConfiguration
import application.entities.SocialMedia
import application.entities.SocialPosts
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.junit.jupiter.api.Test
import java.io.File
import java.time.Instant

/**
 * Renders the desktop app screens and writes them as PNG files so the
 * documentation images can be regenerated instead of captured by hand.
 *
 * Run it through the dedicated Gradle task, which decides where the files go:
 *
 * ```
 * ./gradlew :desktop:generateScreenshots
 * ```
 *
 * The fixture is fixed (static store, static clock, fixed window size), so the
 * same command always renders the same screens.
 */
@OptIn(ExperimentalTestApi::class)
class ScreenshotGalleryTest {
    @Test
    fun `renders every desktop screen into the docs`() =
        runDesktopComposeUiTest(width = WINDOW_WIDTH, height = WINDOW_HEIGHT) {
            setContent {
                provideUiScale {
                    desktopAppShell(galleryStore())
                }
            }

            onNodeWithText("What do you want to do?").assertIsDisplayed()
            saveScreenshot("home")

            onNodeWithTag(homeNavigationTag(AppPage.POSTS)).performClick()
            onNodeWithText("Shipping the docs for social-publisher today.").assertIsDisplayed()
            onNodeWithText("Local-first posting, now with LinkedIn support.").assertIsDisplayed()
            saveScreenshot("posts")

            openFromDrawer("Schedules")
            onNodeWithText("New schedule").assertIsDisplayed()
            saveScreenshot("schedules")

            openFromDrawer("Compose")
            onNode(hasSetTextAction()).performTextInput(SAMPLE_POST)
            onNodeWithText(SAMPLE_POST).assertIsDisplayed()
            saveScreenshot("compose")

            openFromDrawer("Poster")
            onNodeWithText("The poster is disabled.").assertIsDisplayed()
            saveScreenshot("poster")

            openFromDrawer("Configuration")
            onNodeWithText("Store configuration").assertIsDisplayed()
            saveScreenshot("configuration")

            onNodeWithContentDescription("Menu").performClick()
            onNodeWithText("Home").assertIsDisplayed()
            saveScreenshot("navigation")
        }

    private fun SkikoComposeUiTest.openFromDrawer(title: String) {
        onNodeWithContentDescription("Menu").performClick()
        onNodeWithText(title).performClick()
    }

    private fun SkikoComposeUiTest.saveScreenshot(name: String) {
        waitForIdle()
        val data =
            Image
                .makeFromBitmap(captureToImage().asSkiaBitmap())
                .encodeToData(EncodedImageFormat.PNG)
                ?: error("Could not encode the '$name' screenshot as PNG")
        val file = File(OUTPUT_DIR, "$name.png")
        file.parentFile.mkdirs()
        file.writeBytes(data.bytes)
    }

    private fun galleryStore(): SocialPublisherStore {
        val postsRepository = InMemoryPostRepository()
        postsRepository.save(
            arrayListOf(
                SocialPosts(text = "Shipping the docs for social-publisher today."),
                SocialPosts(text = "One jar, three surfaces: desktop, terminal and REST API."),
                SocialPosts(text = "Local-first posting, now with LinkedIn support."),
            ),
        )

        val schedulerRepository = InMemorySchedulerRepository()
        schedulerRepository.save(
            ScheduledItem(
                post = postsRepository.findAll()[0],
                publishDate = Instant.parse("2026-10-02T09:00:00Z"),
                socialMedia = SocialMedia.TWITTER,
            ),
        )
        schedulerRepository.save(
            ScheduledItem(
                post = postsRepository.findAll()[2],
                publishDate = Instant.parse("2026-10-03T14:30:00Z"),
                socialMedia = SocialMedia.LINKEDIN,
            ),
        )

        return SocialPublisherStore(
            postsRepository = postsRepository,
            schedulerRepository = schedulerRepository,
            configurationRepository =
                ConfigurationInMemoryRepository()
                    .apply { save(SocialConfiguration(storage = "csv", timezone = "UTC")) },
            output = MockedOutput(),
            socialNetworks = MockedSocialThirdParty(),
            currentDate = { Instant.parse("2026-10-05T10:00:00Z") },
        )
    }

    private companion object {
        const val WINDOW_WIDTH = 1280
        const val WINDOW_HEIGHT = 800
        const val SAMPLE_POST = "Shipping the docs for social-publisher today."
        val OUTPUT_DIR = File(System.getProperty("screenshots.dir") ?: "../docs/public/screenshots")
    }
}
