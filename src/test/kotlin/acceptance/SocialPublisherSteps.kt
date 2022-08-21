package acceptance

import application.entities.SocialConfiguration
import application.entities.TwitterCredentials
import buildCommandLine
import io.cucumber.java8.En
import io.github.cdimascio.dotenv.dotenv
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.social.twitter.api.impl.TwitterTemplate
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.time.Instant
import kotlin.test.assertContains

class SocialPublisherSteps: En {

    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()
    private val dotenv = dotenv()
    private val twitterCredentials = TwitterCredentials(
        dotenv["TWITTER_CONSUMER_KEY"],
        dotenv["TWITTER_CONSUMER_SECRET"],
        dotenv["TWITTER_TOKEN"],
        dotenv["TWITTER_TOKEN_SECRET"],
    )

    private fun cleanUp() {
        System.setOut(PrintStream(outputStreamCaptor))

        File("data/").listFiles()?.forEach {
            it.delete()
        }
    }

    init {
        lateinit var cmd: CommandLine
        var exitCode: Int? = null

        Given("A new cli") { ->
            cmd = buildCommandLine()
            cleanUp()
        }

        Given("A new cli with date set to {string}") { dateTime: String ->
            cmd = buildCommandLine(Instant.parse(dateTime))
            cleanUp()
        }

        When(
            "I create a post with the text {string}"
        ) { text: String? ->
            exitCode = cmd.execute("post", "-c", text)
        }

        Then(
            "Show successfully message {string}"
        ) { successMessage: String? ->
            assertEquals(0, exitCode)
            assertEquals(successMessage + "\n", outputStreamCaptor.toString())
        }

        Then(
            "Show successfully message"
        ) { docString: String ->
            assertEquals(0, exitCode)
            assertEquals(docString + "\n", outputStreamCaptor.toString())
        }

        Then("I clean the output") {
            outputStreamCaptor.reset()
        }

        Then(
            "Show the created post {string}"
        ) { text: String ->
            exitCode = cmd.execute("post", "-l")
            assertEquals(0, exitCode)
            assertContains(outputStreamCaptor.toString(), text)
        }

        When("I list the scheduled posts") {
            exitCode = cmd.execute("scheduler", "list")
        }

        When("I list the scheduled posts starting from {string}") {
            startDate: String ->
            exitCode = cmd.execute("scheduler", "list", "--start-date", startDate)
        }

        When("I list the scheduled posts with the end date for {string}") {
            endDate: String ->
            exitCode = cmd.execute("scheduler", "list", "--end-date", endDate)
        }

        When(
            "I schedule the post with id {string} to be published at {string}"
        ) { postId: String, dateToBePublished: String ->
            exitCode = cmd.execute("scheduler", "create", "-p", postId, "-d", dateToBePublished)
            assertEquals(0, exitCode)
        }

        Then(
            "Show the scheduled post {string}"
        ) { text: String ->
            exitCode = cmd.execute("scheduler", "list")
            assertEquals(0, exitCode)
            assertContains(outputStreamCaptor.toString(), text)
        }

        Then(
            "I set the post {string} to {string}"
        ) { postId: String, socialMedia: String ->
            exitCode = cmd.execute("poster", "-p", postId, "-s", socialMedia)
            assertEquals(0, exitCode)
            assertContains(outputStreamCaptor.toString(), "Post $postId set to $socialMedia")
        }

        When(
            "I create a configuration of type csv and store files under the name {string}"
        ) { fileName: String ->
            exitCode = cmd.execute("configuration", "-c", """{"storage":"csv","fileName":"$fileName"}""")
            assertEquals(0, exitCode)
            assertContains(outputStreamCaptor.toString(), "Configuration has been stored")
        }

        Then("I list the configuration"
        ) {
            exitCode = cmd.execute("configuration", "-l")
            assertEquals(0, exitCode)
        }

        Then(
            "I see the configuration {string}"
        ) { configuration: String ->
            assertEquals(outputStreamCaptor.toString(), configuration + "\n")
        }

        When("I list the posts") {
            exitCode = cmd.execute("post", "-l")
        }

        Then("Poster should execute routine to send posts") {
            exitCode = cmd.execute("poster", "-r")
        }

        Then("I remove post {string} from twitter") {
                postText: String ->
            val client = TwitterTemplate(
                twitterCredentials.consumerKey,
                twitterCredentials.consumerSecret,
                twitterCredentials.accessToken,
                twitterCredentials.accessTokenSecret
            )

            client.timelineOperations().userTimeline.forEach {
                if (it.text.equals(postText)) {
                    client.timelineOperations().deleteStatus(it.id)
                }
            }
        }

        Given("the twitter credentials in place") {
            val socialConfiguration = SocialConfiguration(
                "twitter",
                "csv",
                twitterCredentials
            )
            val configuration = Json.encodeToString(socialConfiguration)

            exitCode = cmd.execute("configuration", "-c", configuration)
            assertContains(outputStreamCaptor.toString(), "Configuration has been stored")
        }

        Then(
            "Poster should show {string}"
        ) { output: String ->
            exitCode = cmd.execute("poster", "-r")
            assertContains(outputStreamCaptor.toString(), output)
        }

        When(
            "I create a configuration with {string}"
        ) { jsonConfiguration: String ->
            exitCode = cmd.execute("configuration", "-c", jsonConfiguration)
            assertContains(outputStreamCaptor.toString(), "Configuration has been stored")
        }

        Then(
            "I remove schedule from post with id {string}"
        ) { scheduleId: String ->
            exitCode = cmd.execute("scheduler", "delete", "-id", scheduleId)
            assertContains(outputStreamCaptor.toString(), "Schedule 1 has been removed from post 1")
        }

        Then(
            "I list scheduled posts with {string}"
        ) { params: String ->
            exitCode = cmd.execute("scheduler", params)
            assertEquals(0, exitCode)
        }
    }
}
