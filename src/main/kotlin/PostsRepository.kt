import socialPosts.SocialPosts

interface PostsRepository {

    fun save(posts: ArrayList<SocialPosts>): Boolean

    fun findAll(): ArrayList<SocialPosts>
    fun findById(postId: String): SocialPosts?
}
