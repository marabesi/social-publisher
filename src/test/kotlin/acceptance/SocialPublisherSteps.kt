package acceptance

import buildCommandLine
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertContains

class SocialPublisherSteps: En {

//    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    init {
        lateinit var cmd: CommandLine
        var exitCode: Int? = null

        Given("A new cli"){ ->
            cmd  = buildCommandLine()

            System.setOut(PrintStream(outputStreamCaptor))

            File("data/").listFiles()?.forEach {
                it.delete()
            }
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

        When(
            "I define the system to use the date {string}"
        ) { dateTime: String ->
            Instant.now(
                Clock.fixed(
                Instant.parse(dateTime),
                ZoneOffset.UTC)
            )
        }

        Then("Poster should send post {string} to twitter") {
            postId: String ->
            exitCode = cmd.execute("poster", "-r")
            assertEquals(outputStreamCaptor.toString(), "Post $postId sent to twitter")
        }
    }
}