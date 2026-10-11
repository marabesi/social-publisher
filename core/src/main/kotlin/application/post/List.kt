package application.post

import application.Output
import application.entities.SocialPosts
import application.order.Order
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository

const val ALLOWED_CHARACTERS_TO_SHOW = 50

class List(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
    private val cliOutput: Output,
    private val search: PostSearch = PostSearch(),
    private val order: Order<SocialPosts>? = null,
) {
    fun invoke(): String {
        val found = Search(postsRepository, schedulerRepository).invoke(search)
        val findAll = order?.apply(ArrayList(found)) ?: found
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
