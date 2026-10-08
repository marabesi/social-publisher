package application.post

import application.Messages
import application.Output
import application.entities.SocialPosts
import application.persistence.PostsRepository

class Update(
    private val postsRepository: PostsRepository,
    private val cliOutput: Output,
) {
    fun invoke(
        postId: String,
        text: String,
    ): String {
        if (postId.isBlank() || text.isBlank()) {
            return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
        }

        val post = postsRepository.findById(postId) ?: return cliOutput.write("Couldn't find post with id $postId")

        if (!postsRepository.update(SocialPosts(post.id, text))) {
            return cliOutput.write("Couldn't find post with id $postId")
        }

        return cliOutput.write("Post has been updated")
    }
}
