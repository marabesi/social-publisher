package application.socialnetwork

import application.entities.ScheduledItem

interface SocialThirdParty {

    fun send(scheduledItem: ScheduledItem): Boolean
}
