package application.post

import application.Messages
import application.Output
import application.persistence.PostsRepository

class Delete(
    private val postsRepository: PostsRepository,
    private val cliOutput: Output,
) {
    fun invoke(postId: String): String {
        if (postId.isNotBlank()) {
            val deleted = postsRepository.deleteById(postId)
            if (deleted != null) {
                return cliOutput.write("Post $postId has been removed")
            }
        }

        return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
}
