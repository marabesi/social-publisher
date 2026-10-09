package application.post

import application.Output
import application.persistence.PostsRepository

const val ALLOWED_CHARACTERS_TO_SHOW = 50

class List(
    private val postsRepository: PostsRepository,
    private val cliOutput: Output,
) {
    fun invoke(): String {
        val findAll = postsRepository.findAll()
        if (findAll.isEmpty()) {
            return cliOutput.write("No post found")
        }

        var result = ""
        var index = 1
        for (post in findAll) {
            val isLast: Boolean = index == findAll.size
            val text =
                if (post.text.length > ALLOWED_CHARACTERS_TO_SHOW) {
                    post.text.substring(0, ALLOWED_CHARACTERS_TO_SHOW) + "..."
                } else {
                    post.text
                }

            result += "${post.id}. $text (${post.text.length}/${PostLimits.MAX_CHARACTERS})"
            if (!isLast) {
                result += "\n"
            }
            ++index
        }
        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}
