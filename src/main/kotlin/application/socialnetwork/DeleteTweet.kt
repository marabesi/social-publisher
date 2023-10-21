package application.socialnetwork

interface DeleteTweet {

    fun deleteTweetByTweetText(text: String): Boolean
    fun deleteTweetByTweetId(id: String): Boolean
}
