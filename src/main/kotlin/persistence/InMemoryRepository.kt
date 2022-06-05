package persistence

import PostsRepository
import socialPosts.SocialPosts

class InMemoryRepository: PostsRepository {

    private var storedPosts: ArrayList<SocialPosts> = arrayListOf();

    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        for (post in posts) {
            post.id = storedPosts.size + 1
            storedPosts.add(post)
        }
        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        return storedPosts
    }

    override fun findById(postId: String): SocialPosts? {
        var socialPosts: SocialPosts? = null
        storedPosts.forEach {
            if (it.id.toString().equals(postId)) {
                socialPosts = it
            }
        }
        return  socialPosts
    }
}
