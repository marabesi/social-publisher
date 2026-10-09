package adapters.inbound.rest.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info =
        Info(
            title = "Social Publisher API",
            version = "1.0.0",
            description = "Schedule and publish posts to social networks.",
        ),
)
class OpenApiConfiguration
