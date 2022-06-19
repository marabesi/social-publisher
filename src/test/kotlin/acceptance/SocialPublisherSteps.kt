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

        Given("A new cli"){ ->
            cmd  = buildCommandLine()
            cleanUp()
        }

        Given("A new cli with date set to {string}"){
            dateTime: String ->
            cmd  = buildCommandLine(Instant.parse(dateTime))
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
            assertEquals(successMessage, outputStreamCaptor.toString())
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

        When(
            "I schedule the post with id {string} to be published at {string}"
        ) { postId: String, dateToBePublished: String ->
            exitCode = cmd.execute("scheduler", "-c", "-p", postId, "-d", dateToBePublished)
            assertEquals(0, exitCode)
        }

        Then(
            "Show the scheduled post {string}"
        ) { text: String ->
            exitCode = cmd.execute("scheduler", "-l")
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
            assertEquals(outputStreamCaptor.toString(), configuration)
        }

        When("I list the posts") {
            exitCode = cmd.execute("post", "-l")
        }

        Then("Poster should send post {string} with text {string} to twitter") {
            postId: String,
            postText: String ->
            exitCode = cmd.execute("poster", "-r")
            assertEquals(outputStreamCaptor.toString(), "Post $postId sent to twitter")

            val client = TwitterTemplate(
                twitterCredentials.consumerKey,
                twitterCredentials.consumerSecret,
                twitterCredentials.accessToken,
                twitterCredentials.accessTokenSecret
            )

            client.timelineOperations().homeTimeline.forEach {
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
    }
}