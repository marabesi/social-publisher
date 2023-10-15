package application.socialnetwork

import application.entities.SocialPosts

interface CreateTweet {
    fun sendTweet(text: String): SocialPosts
}
