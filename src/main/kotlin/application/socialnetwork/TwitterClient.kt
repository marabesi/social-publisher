package application.socialnetwork

interface TwitterClient {
    fun sendTweet(text: String): Boolean
}