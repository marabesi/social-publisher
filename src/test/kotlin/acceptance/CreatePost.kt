package acceptance

import buildCommandLine
import io.cucumber.java8.En
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

        Then("I clean the output") {
            sw.buffer.setLength(0)
        }

        When(
            "I create a post with the title {string}"
        ) { _: String? ->
            exitCode = cmd.execute("post", "-c", "Hello")
        }

        Then(
            "Show successfully message {string}"
        ) { _: String? ->
            assertEquals(0, exitCode)
            assertEquals("Post has been created", sw.toString())
        }

        Then("Show the created post") {
            exitCode = cmd.execute("post", "-l")
            assertEquals(0, exitCode)
            assertContains("1. Hello", sw.toString())
        }
    }
}