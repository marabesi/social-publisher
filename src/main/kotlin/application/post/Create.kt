package application.post

import application.Messages
import application.Output
import application.entities.SocialPosts
import application.persistence.PostsRepository

class Create(
    private val postsRepository: PostsRepository,
    private val cliOutput: Output
) {

    fun invoke(text: String): String {
        if (text.isNotBlank()) {
            postsRepository.save(arrayListOf(SocialPosts(null, text)))
            return cliOutput.write("Post has been created")
        }

        return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
}
