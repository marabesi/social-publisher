package acceptance

import buildCommandLine
import io.cucumber.java8.En
import io.cucumber.java8.PendingException
import picocli.CommandLine
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

        Then("Show the created post") {
            exitCode = cmd.execute("post", "-l")
            assertEquals(0, exitCode, "Exit code is not correct check the cli lib for details")
            assertContains("1. Hello", sw.toString())
        }

        Then("I clean the output") {
            sw.buffer.setLength(0)
        }
    }
}