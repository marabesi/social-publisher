package adapters.inbound.rest

import adapters.inbound.rest.dto.MessageResponse
import application.Output
import application.persistence.SchedulerRepository
import application.poster.Executor
import application.socialnetwork.SocialThirdPartyProvider
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/api/poster")
class PosterController(
    private val schedulerRepository: SchedulerRepository,
    private val output: Output,
    private val socialThirdPartyProvider: SocialThirdPartyProvider,
) {
    @PostMapping("/run")
    fun run(): MessageResponse {
        val message = Executor(schedulerRepository, output, Instant.now(), socialThirdPartyProvider).invoke()
        return MessageResponse(message)
    }
}
