import SocialPosts.SocialPosts

interface PostsRepository {

    fun save(post: SocialPosts): Boolean

    fun findAll(): ArrayList<SocialPosts>
}
