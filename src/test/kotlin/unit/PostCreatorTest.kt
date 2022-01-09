package unit

import Post
import org.junit.jupiter.api.Test
import picocli.CommandLine
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class PostCreatorTest {

    @Test
    fun `should create post with the desired text`() {
        val cmd = CommandLine(Post())

        val sw = StringWriter()
        cmd.out = PrintWriter(sw)
        cmd.err = PrintWriter(sw)

        cmd.execute()

        assertEquals("""
            Post has been created
        """.trimIndent(), sw.toString())
    }
}