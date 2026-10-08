package application.persistence

import application.entities.SocialPosts

interface PostsRepository {
    fun save(posts: ArrayList<SocialPosts>): Boolean

    fun findAll(): ArrayList<SocialPosts>

    fun findById(postId: String): SocialPosts?

    fun deleteById(postId: String): SocialPosts?

    fun update(post: SocialPosts): Boolean
}
