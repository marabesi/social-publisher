package acceptance

import buildCommandLine
import io.cucumber.java8.En
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import kotlin.test.assertContains
import kotlin.test.assertEquals


class CreatePost: En {

//    private val standardOut = System.out
    private val outputStreamCaptor: ByteArrayOutputStream = ByteArrayOutputStream()

    init {
        lateinit var cmd: CommandLine
        var exitCode: Int? = null

        Given("A new cli"){ ->
            cmd  = buildCommandLine()

            System.setOut(PrintStream(outputStreamCaptor))

            val data = File("data/social-production.csv")
            data.delete();
        }

        When(
            "I create a post with the text {string}"
        ) { text: String? ->
            exitCode = cmd.execute("post", "-c", text)
        }

        Then(
            "Show successfully message {string}"
        ) { successMessage: String? ->
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
            assertEquals(successMessage, outputStreamCaptor.toString())
        }

        Then("I clean the output") {
            outputStreamCaptor.reset()
        }

        Then(
            "Show the created post {string}"
        ) { text: String ->
            exitCode = cmd.execute("post", "-l")
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
            assertContains(outputStreamCaptor.toString(), text)
        }

        When(
            "I and schedule the post with id {string} to be published at {string}"
        ) { postId: String, dateToBePublished: String ->
            exitCode = cmd.execute("scheduler", "-p", postId, "-d", dateToBePublished)
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
        }

        Then(
            "Show the scheduled post {string}"
        ) { text: String ->
            exitCode = cmd.execute("scheduler", "-l")
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
            assertContains(outputStreamCaptor.toString(), text)
        }
    }
}