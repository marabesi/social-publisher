package acceptance

import buildCommandLine
import io.cucumber.java8.En
import picocli.CommandLine
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CreatePost: En {

    init {
        lateinit var cmd: CommandLine
        lateinit var sw: StringWriter
        var exitCode: Int? = null

        Given("A new cli"){ ->
            cmd  = buildCommandLine()

            sw = StringWriter()
            cmd.out = PrintWriter(sw)
            cmd.err = PrintWriter(sw)

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
            assertEquals(successMessage, sw.toString())
        }

        Then("I clean the output") {
            sw.buffer.setLength(0)
        }

        Then(
            "Show the created post {string}"
        ) { text: String ->
            exitCode = cmd.execute("post", "-l")
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
            assertContains(sw.toString(), text)
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
            assertContains(sw.toString(), text)
        }
    }
}