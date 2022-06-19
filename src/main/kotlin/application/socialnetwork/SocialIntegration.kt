package application.socialnetwork

interface SocialIntegration {
    fun sendTweet(text: String): Boolean
}