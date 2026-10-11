package adapters.outbound.googledrive

import application.entities.SocialPosts
import application.persistence.PostsRepository

class GoogleDrivePostsRepository(
    private val delegate: PostsRepository,
    private val sync: GoogleDriveSync,
) : PostsRepository {
    override fun save(posts: ArrayList<SocialPosts>): Boolean = delegate.save(posts).also { sync.sync() }

    override fun findAll(): ArrayList<SocialPosts> = delegate.findAll()

    override fun findById(postId: String): SocialPosts? = delegate.findById(postId)

    override fun deleteById(postId: String): SocialPosts? = delegate.deleteById(postId).also { sync.sync() }

    override fun update(post: SocialPosts): Boolean = delegate.update(post).also { sync.sync() }
}
