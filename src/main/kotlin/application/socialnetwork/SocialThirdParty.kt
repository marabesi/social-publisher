package application.socialnetwork

import application.entities.ScheduledItem
import application.entities.SocialPosts

interface SocialThirdParty {

    fun send(scheduledItem: ScheduledItem): SocialPosts
}