package persistence

import PostsRepository
import SocialPosts.SocialPosts

class InMemoryRepository: PostsRepository {

    private val posts: ArrayList<SocialPosts> = arrayListOf();

    override fun save(post: SocialPosts): Boolean {
        posts.add(post)
        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        return posts
    }
}
