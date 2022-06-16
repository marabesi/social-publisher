package application.post

import application.Output
import application.persistence.PostsRepository

class List(
    private val postsRepository: PostsRepository,
    private val cliOutput: Output
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

            result += when {
                post.text.length > 50 -> {
                    post.id.toString() + ". " + post.text.substring(0, 50) + "..."
                }
                else -> {
                    post.id.toString() + ". " + post.text
                }
            }
            if (!isLast) {
                result += "\n"
            }
            ++index
        }
        val output = result.trimIndent()
        return cliOutput.write(output)
    }
}