package application.socialnetwork

import application.entities.SocialMedia

interface SocialThirdPartyProvider {
    fun forMedia(socialMedia: SocialMedia): SocialThirdParty
}
