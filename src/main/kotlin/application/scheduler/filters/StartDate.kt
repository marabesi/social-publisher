package application.scheduler.filters

import application.entities.ScheduledItem
import java.time.Instant

class StartDate(private val currentTime: Instant) : Criterion {
    override fun getFilter(): Filter {
        return Filter("publishDate", EQUALS_AND_GREATER_THAN, currentTime)
    }

    override fun applyPredicateFor(item: ScheduledItem): Boolean {
        val parse = getFilter()

        val date = parse.value as Instant
        return item.publishDate >= date
    }
}
