package adapters.inbound.rest

import adapters.inbound.rest.dto.LinkedInTokenRequest
import adapters.inbound.rest.dto.MessageResponse
import application.Output
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.AuthorizationUrl
import application.socialnetwork.ConnectLinkedIn
import application.socialnetwork.ExchangeLinkedInAuthorization
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/linkedin")
class LinkedinController(
    private val configurationRepository: ConfigurationRepository,
    private val exchange: ExchangeLinkedInAuthorization,
    private val output: Output,
) {
    @GetMapping("/authorization-url")
    fun authorizationUrl(
        @RequestParam(required = false) state: String?,
    ): MessageResponse =
        MessageResponse(
            AuthorizationUrl(configurationRepository, output).invoke(state ?: AuthorizationUrl.DEFAULT_STATE),
        )

    @PostMapping("/token")
    fun token(
        @RequestBody request: LinkedInTokenRequest,
    ): MessageResponse {
        val message = ConnectLinkedIn(configurationRepository, exchange, output).invoke(request.code)
        return MessageResponse(message)
    }
}
