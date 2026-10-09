package restapi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiApplicationTest {
    @Autowired
    lateinit var context: ApplicationContext

    @LocalServerPort
    var port: Int = 0

    private val httpClient: HttpClient = HttpClient.newHttpClient()

    @Test
    fun `context loads`() {
        assertNotNull(context)
    }

    @Test
    fun `serves the swagger ui`() {
        val response = get("/swagger-ui/index.html")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("Swagger UI"))
    }

    @Test
    fun `serves the openapi documentation`() {
        val response = get("/v3/api-docs")

        assertEquals(200, response.statusCode())
        assertTrue(response.body().contains("Social Publisher API"))
    }

    private fun get(path: String): HttpResponse<String> =
        httpClient.send(
            HttpRequest
                .newBuilder(URI("http://localhost:$port$path"))
                .GET()
                .build(),
            HttpResponse.BodyHandlers.ofString(),
        )
}
