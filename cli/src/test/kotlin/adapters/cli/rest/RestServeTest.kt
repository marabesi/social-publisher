package adapters.cli.rest

import adapters.inbound.cli.rest.RestServe
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import picocli.CommandLine

class RestServeTest {
    @Test
    fun `starts the rest server with no extra arguments`() {
        val serve = RestServe()
        var passed: Array<String>? = null
        serve.launcher = { passed = it }

        CommandLine(serve).execute()

        assertArrayEquals(emptyArray(), passed)
    }

    @Test
    fun `passes the unmatched arguments to the spring launcher`() {
        val serve = RestServe()
        var passed: Array<String>? = null
        serve.launcher = { passed = it }

        CommandLine(serve).execute("--server.port=9090", "--spring.profiles.active=test")

        assertArrayEquals(arrayOf("--server.port=9090", "--spring.profiles.active=test"), passed)
    }
}
