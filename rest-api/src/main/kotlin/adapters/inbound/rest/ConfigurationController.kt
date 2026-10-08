package adapters.inbound.rest

import adapters.inbound.rest.dto.MessageResponse
import application.Output
import application.configuration.Create
import application.entities.SocialConfiguration
import application.persistence.configuration.ConfigurationRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/configuration")
class ConfigurationController(
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
) {
    @PostMapping
    fun store(
        @RequestBody configuration: SocialConfiguration,
    ): MessageResponse =
        MessageResponse(
            Create(output, configurationRepository).invoke(Json.encodeToString(configuration)),
        )

    @GetMapping
    fun get(): SocialConfiguration = configurationRepository.find()
}
