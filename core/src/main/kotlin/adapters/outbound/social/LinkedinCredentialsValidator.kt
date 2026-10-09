package adapters.outbound.social

import application.entities.LinkedInCredentials
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.SocialThirdParty
import jakarta.inject.Inject

open class LinkedinCredentialsValidator
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
        private val linkedin: Linkedin,
    ) : SocialThirdParty {
        override fun send(scheduledItem: ScheduledItem): SocialPosts {
            val configuration = configurationRepository.find()
            validate(configuration.linkedin)

            return linkedin.publish(scheduledItem.post.text)
        }

        private fun validate(linkedin: LinkedInCredentials?) {
            val missingParameter = firstMissingParameter(linkedin)

            if (missingParameter != null) {
                throw MissingConfigurationSetup(missingParameter)
            }
        }

        private fun firstMissingParameter(linkedin: LinkedInCredentials?): String? =
            when {
                linkedin == null -> "linkedin"
                linkedin.accessToken.isEmpty() -> "access token"
                linkedin.authorUrn.isEmpty() -> "author urn"
                else -> null
            }
    }
