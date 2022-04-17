package persistence

import PostsRepository
import socialPosts.SocialPosts

class InMemoryRepository: PostsRepository {

    private var storedPosts: ArrayList<SocialPosts> = arrayListOf();

    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        for (post in posts) {
            storedPosts.add(post)
        }
        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        return storedPosts
    }
}