package restapi

import org.springframework.boot.runApplication

fun serveRest(arguments: Array<String>) {
    runApplication<RestApiApplication>(*arguments)
}
