package application.post.order

import application.entities.SocialPosts
import application.order.Direction
import application.order.Order

class PostIdOrder(
    private val direction: Direction,
) : Order<SocialPosts> {
    override fun apply(items: ArrayList<SocialPosts>): ArrayList<SocialPosts> =
        ArrayList(
            when (direction) {
                Direction.ASC -> items.sortedWith(ASCENDING_BY_ID)
                Direction.DESC -> items.sortedWith(DESCENDING_BY_ID)
            },
        )

    private companion object {
        val ASCENDING_BY_ID: Comparator<SocialPosts> =
            compareBy<SocialPosts> { it.id?.toLongOrNull() == null }
                .thenBy { it.id?.toLongOrNull() ?: 0L }
        val DESCENDING_BY_ID: Comparator<SocialPosts> =
            compareBy<SocialPosts> { it.id?.toLongOrNull() == null }
                .thenByDescending { it.id?.toLongOrNull() ?: 0L }
    }
}
