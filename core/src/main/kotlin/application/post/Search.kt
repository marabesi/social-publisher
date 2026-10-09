package application.post

import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository

class Search(
    private val postsRepository: PostsRepository,
    private val schedulerRepository: SchedulerRepository,
) {
    fun invoke(search: PostSearch = PostSearch()): kotlin.collections.List<SocialPosts> {
        val schedules = if (search.socialMedia == null) emptyList() else schedulerRepository.findAll()
        return search.filter(postsRepository.findAll(), schedules)
    }
}
