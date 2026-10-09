package adapters.inbound.rest

import adapters.inbound.rest.dto.HomeResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping("/")
    fun home(): HomeResponse =
        HomeResponse(
            name = "Social Publisher API",
            resources =
                linkedMapOf(
                    "posts" to "/api/posts",
                    "schedules" to "/api/schedules",
                    "poster" to "/api/poster/run",
                    "configuration" to "/api/configuration",
                ),
        )
}
