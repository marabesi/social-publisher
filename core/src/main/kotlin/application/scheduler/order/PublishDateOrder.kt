package application.scheduler.order

import application.entities.ScheduledItem

class PublishDateOrder(
    private val direction: Direction,
) : Order {
    override fun apply(items: ArrayList<ScheduledItem>): ArrayList<ScheduledItem> =
        ArrayList(
            when (direction) {
                Direction.ASC -> items.sortedBy { it.publishDate }
                Direction.DESC -> items.sortedByDescending { it.publishDate }
            },
        )
}
