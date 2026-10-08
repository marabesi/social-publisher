package desktop

import application.entities.ScheduledItem
import application.entities.SocialPosts
import application.socialnetwork.SocialThirdParty

class MockedSocialThirdParty : SocialThirdParty {
    override fun send(scheduledItem: ScheduledItem): SocialPosts = scheduledItem.post
}
