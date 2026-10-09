package application.socialnetwork

import application.entities.SocialMedia

class UnsupportedSocialNetwork(
    socialMedia: SocialMedia,
) : Throwable("No publisher configured for ${socialMedia.displayName}")
