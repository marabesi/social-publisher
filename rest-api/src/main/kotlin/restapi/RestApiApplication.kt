package restapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["adapters.inbound.rest"])
class RestApiApplication

fun main(args: Array<String>) {
    runApplication<RestApiApplication>(*args)
}
