package acceptance

import buildCommandLine
import io.cucumber.java8.En
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class CreatePost: En {
    private val cmd = buildCommandLine()

    init {
        var exitCode: Int? = null
        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        When(
            "I create a post with the title {string}"
        ) { _: String? ->
            exitCode = cmd.execute("post", "-c", "hello")
        }

        Then(
            "Show successfull menssage {string}"
        ) { _: String? ->
            assertEquals(0, exitCode)
            assertEquals("Post has been created", sw.toString())
        }
    }
}