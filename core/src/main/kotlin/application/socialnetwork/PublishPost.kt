package application.socialnetwork

import application.entities.SocialPosts

interface PublishPost {
    fun publish(text: String): SocialPosts
}
