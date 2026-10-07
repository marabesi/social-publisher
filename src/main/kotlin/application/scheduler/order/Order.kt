package application.scheduler.order

import application.entities.ScheduledItem

interface Order {
    fun apply(items: ArrayList<ScheduledItem>): ArrayList<ScheduledItem>
}
