package adapters.outbound.bluesky

import application.entities.BlueskyCredentials
import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.persistence.configuration.ConfigurationRepository
import application.socialnetwork.MissingConfigurationSetup
import application.socialnetwork.SocialThirdParty
import jakarta.inject.Inject

open class BlueskyCredentialsValidator
    @Inject
    constructor(
        private val configurationRepository: ConfigurationRepository,
        private val bluesky: Bluesky,
    ) : SocialThirdParty {
        override fun send(scheduledItem: ScheduledItem): SocialPosts {
            val configuration = configurationRepository.find()
            validate(configuration.bluesky)

            return bluesky.publish(scheduledItem.post.text)
        }

        private fun validate(bluesky: BlueskyCredentials?) {
            val missingParameter = firstMissingParameter(bluesky)

            if (missingParameter != null) {
                throw MissingConfigurationSetup(missingParameter)
            }
        }

        private fun firstMissingParameter(bluesky: BlueskyCredentials?): String? =
            when {
                bluesky == null -> "bluesky"
                bluesky.identifier.isEmpty() -> "identifier"
                bluesky.password.isEmpty() -> "password"
                else -> null
            }
    }
