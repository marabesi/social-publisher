package desktop

import MockedOutput
import adapters.outbound.inmemory.ConfigurationInMemoryRepository
import adapters.outbound.inmemory.InMemoryPostRepository
import adapters.outbound.inmemory.InMemorySchedulerRepository
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.runComposeUiTest
import application.entities.LinkedInCredentials
import application.entities.SocialConfiguration
import application.entities.TwitterCredentials
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@OptIn(ExperimentalTestApi::class)
class ConfigurationPageTest {
    @Test
    fun `shows a field for every editable configuration property`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { configurationPage(store) }

            onNodeWithText("Storage").assertExists()
            onNodeWithText("Timezone").assertExists()
            onNodeWithText("Consumer key").assertExists()
            onNodeWithText("Consumer secret").assertExists()
            onNodeWithText("Access token").assertExists()
            onNodeWithText("Access token secret").assertExists()
            onNodeWithText("LinkedIn client id").assertExists()
            onNodeWithText("LinkedIn client secret").assertExists()
            onNodeWithText("LinkedIn redirect uri").assertExists()
            onNodeWithText("LinkedIn access token").assertExists()
            onNodeWithText("LinkedIn author urn").assertExists()
            onNodeWithText("Connect LinkedIn").assertExists()
        }

    @Test
    fun `does not expose the file name property`() =
        runComposeUiTest {
            val store = storeWith(SocialConfiguration(fileName = "prod"))

            setContent { configurationPage(store) }

            onNodeWithText("fileName").assertDoesNotExist()
            onNodeWithText("File name").assertDoesNotExist()
        }

    @Test
    fun `prefills the fields from the stored configuration`() =
        runComposeUiTest {
            val store =
                storeWith(
                    SocialConfiguration(
                        fileName = "prod",
                        storage = "csv",
                        timezone = "Europe/Madrid",
                        twitter =
                            TwitterCredentials(
                                consumerKey = "consumer-key",
                                consumerSecret = "consumer-secret",
                                accessToken = "access-token",
                                accessTokenSecret = "access-token-secret",
                            ),
                        linkedin =
                            LinkedInCredentials(
                                accessToken = "linkedin-access-token",
                                authorUrn = "urn:li:person:123",
                            ),
                    ),
                )

            setContent { configurationPage(store) }

            onNodeWithText("Europe/Madrid").assertExists()
            onNodeWithText("consumer-key").assertExists()
            onNodeWithText("consumer-secret").assertExists()
            onNodeWithText("access-token").assertExists()
            onNodeWithText("access-token-secret").assertExists()
            onNodeWithText("linkedin-access-token").assertExists()
            onNodeWithText("urn:li:person:123").assertExists()
        }

    @Test
    fun `stores the edited configuration and keeps the file name`() =
        runComposeUiTest {
            val store = storeWith(SocialConfiguration(fileName = "prod", storage = "csv", timezone = "UTC"))

            setContent { configurationPage(store) }

            onNodeWithText("UTC").performTextReplacement("Europe/Madrid")
            onNodeWithText("Store configuration").performClick()

            assertEquals("Europe/Madrid", store.configuration()?.timezone)
            assertEquals("prod", store.configuration()?.fileName)
        }

    @Test
    fun `stores the defaults when no configuration exists yet`() =
        runComposeUiTest {
            val store = storeWith()

            setContent { configurationPage(store) }

            onNodeWithText("Store configuration").assertIsDisplayed()
            onNodeWithText("Store configuration").performClick()

            assertEquals("csv", store.configuration()?.storage)
            assertEquals("UTC", store.configuration()?.timezone)
        }

    private fun storeWith(configuration: SocialConfiguration? = null): SocialPublisherStore {
        val configurationRepository = ConfigurationInMemoryRepository()
        configuration?.let { configurationRepository.save(it) }

        return SocialPublisherStore(
            postsRepository = InMemoryPostRepository(),
            schedulerRepository = InMemorySchedulerRepository(),
            configurationRepository = configurationRepository,
            output = MockedOutput(),
            socialNetworks = MockedSocialThirdParty(),
        )
    }
}
