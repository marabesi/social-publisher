package application.socialnetwork

class MissingConfigurationSetup(
    private val missingParameterName: String
) : Throwable(
    message = "Missing required configuration: $missingParameterName"
)
