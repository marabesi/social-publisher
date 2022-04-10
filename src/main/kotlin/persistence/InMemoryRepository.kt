package persistence

import PostsRepository
import socialPosts.SocialPosts

class InMemoryRepository: PostsRepository {

    private var posts: ArrayList<SocialPosts> = arrayListOf();

    override fun save(post: ArrayList<SocialPosts>): Boolean {
        posts = post
        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        return posts
    }
}
