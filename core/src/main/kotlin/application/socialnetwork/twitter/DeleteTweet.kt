package application.socialnetwork.twitter

interface DeleteTweet {
    fun deleteTweetByTweetText(text: String): Boolean

    fun deleteTweetByTweetId(id: String): Boolean
}
