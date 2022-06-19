package application.socialnetwork

class UnauthorizedAttemptToTweet(
    private val details: String
): Throwable(
    message = "Unauthorized attempt to send a tweet, verify your credentials: $details"
)