package desktop

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.socialnetwork.SocialThirdParty
import application.socialnetwork.SocialThirdPartyProvider

class MockedSocialThirdParty :
    SocialThirdParty,
    SocialThirdPartyProvider {
    override fun send(scheduledItem: ScheduledItem): SocialPosts = scheduledItem.post

    override fun forMedia(socialMedia: SocialMedia): SocialThirdParty = this
}
