package restapi

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class RestApiApplicationTest {
    @Autowired
    lateinit var context: ApplicationContext

    @Test
    fun `context loads`() {
        assertNotNull(context)
    }
}
