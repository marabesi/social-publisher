package adapters.outbound.inmemory

import application.entities.SocialPosts
import application.persistence.PostsRepository

class InMemoryPostRepository : PostsRepository {
    private var storedPosts: ArrayList<SocialPosts> = arrayListOf()

    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        for (post in posts) {
            val id = storedPosts.size + 1
            post.id = id.toString()
            storedPosts.add(post)
        }
        return true
    }

    override fun findAll(): ArrayList<SocialPosts> = storedPosts

    override fun findById(postId: String): SocialPosts? {
        var socialPosts: SocialPosts? = null
        storedPosts.forEach {
            if (it.id.toString().equals(postId)) {
                socialPosts = it
            }
        }
        return socialPosts
    }

    override fun deleteById(postId: String): SocialPosts? {
        val post = findById(postId) ?: return null
        storedPosts.remove(post)
        return post
    }

    override fun update(post: SocialPosts): Boolean {
        val index = storedPosts.indexOfFirst { it.id == post.id }
        if (index < 0) {
            return false
        }
        storedPosts[index] = post
        return true
    }
}
