package acceptance

import buildCommandLine
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertContains

class CreatePost: En {

//    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    init {
        lateinit var cmd: CommandLine
        var exitCode: Int? = null

        Given("A new cli"){ ->
            cmd  = buildCommandLine()

            System.setOut(PrintStream(outputStreamCaptor))

            File("data/social-production.csv").delete()
            File("data/scheduler-social-production.csv").delete()
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
            exitCode = cmd.execute("scheduler", "-p", postId, "-d", dateToBePublished)
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

        Then("I list the configuration from {string}"
        ) {
            exitCode = cmd.execute("configuration", "-l")
            assertEquals(0, exitCode)
        }

        Then(
            "I see the configuration {string}"
        ) { configuration: String ->
            assertContains(outputStreamCaptor.toString(), configuration)
        }

    }
}