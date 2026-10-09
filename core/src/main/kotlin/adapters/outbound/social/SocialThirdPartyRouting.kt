package adapters.outbound.social

import application.entities.SocialMedia
import application.socialnetwork.SocialThirdParty
import application.socialnetwork.SocialThirdPartyProvider
import application.socialnetwork.UnsupportedSocialNetwork
import jakarta.inject.Inject

class SocialThirdPartyRouting
    @Inject
    constructor(
        private val networks: Map<SocialMedia, SocialThirdParty>,
    ) : SocialThirdPartyProvider {
        override fun forMedia(socialMedia: SocialMedia): SocialThirdParty =
            networks[socialMedia] ?: throw UnsupportedSocialNetwork(socialMedia)
    }
