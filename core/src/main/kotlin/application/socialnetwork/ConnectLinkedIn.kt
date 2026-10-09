package application.socialnetwork

import application.Messages
import application.Output
import application.entities.LinkedInCredentials
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration

class ConnectLinkedIn(
    private val configurationRepository: ConfigurationRepository,
    private val exchange: ExchangeLinkedInAuthorization,
    private val output: Output,
) {
    fun invoke(code: String): String {
        if (code.isBlank()) {
            return output.write(Messages.MISSING_REQUIRED_FIELDS)
        }

        val configuration =
            try {
                configurationRepository.find()
            } catch (_: MissingConfiguration) {
                return output.write("Missing required configuration: linkedin")
            }

        val linkedin = configuration.linkedin
        val missing = firstMissing(linkedin)
        if (missing != null) {
            return output.write("Missing required configuration: $missing")
        }

        val token = exchange.exchange(code.trim())

        configurationRepository.save(
            configuration.copy(
                linkedin =
                    linkedin!!.copy(
                        accessToken = token.accessToken,
                        authorUrn = token.authorUrn,
                    ),
            ),
        )

        return output.write("LinkedIn account connected")
    }

    private fun firstMissing(linkedin: LinkedInCredentials?): String? =
        when {
            linkedin == null -> "linkedin"
            linkedin.clientId.isEmpty() -> "linkedin client id"
            linkedin.clientSecret.isEmpty() -> "linkedin client secret"
            linkedin.redirectUri.isEmpty() -> "linkedin redirect uri"
            else -> null
        }
}
