package application.socialnetwork

import application.entities.SocialPosts

interface TwitterClient {
    fun sendTweet(text: String): SocialPosts
}
