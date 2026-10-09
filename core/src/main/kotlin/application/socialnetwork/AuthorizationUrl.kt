package application.socialnetwork

import application.Output
import application.entities.LinkedInCredentials
import application.persistence.configuration.ConfigurationRepository
import application.persistence.configuration.MissingConfiguration

class AuthorizationUrl(
    private val configurationRepository: ConfigurationRepository,
    private val output: Output,
) {
    fun invoke(state: String = DEFAULT_STATE): String {
        val linkedin =
            try {
                configurationRepository.find().linkedin
            } catch (_: MissingConfiguration) {
                null
            }

        val missing = firstMissing(linkedin)
        if (missing != null) {
            return output.write("Missing required configuration: $missing")
        }

        return output.write(
            LinkedInOAuth.authorizationUrl(
                clientId = linkedin!!.clientId,
                redirectUri = linkedin.redirectUri,
                state = state,
            ),
        )
    }

    private fun firstMissing(linkedin: LinkedInCredentials?): String? =
        when {
            linkedin == null -> "linkedin"
            linkedin.clientId.isEmpty() -> "linkedin client id"
            linkedin.redirectUri.isEmpty() -> "linkedin redirect uri"
            else -> null
        }

    companion object {
        const val DEFAULT_STATE = "social-publisher"
    }
}
