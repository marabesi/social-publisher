import SocialPosts.SocialPosts

class PostsRepository {

    private val posts: ArrayList<SocialPosts> = arrayListOf();

    fun save(post: SocialPosts): Boolean {
        posts.add(post)
        return true
    }

    fun findAll(): ArrayList<SocialPosts> {
        return posts
    }
}
