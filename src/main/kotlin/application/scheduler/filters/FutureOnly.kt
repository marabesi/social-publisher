package application.scheduler.filters

import application.entities.ScheduledItem
import java.time.Instant

class FutureOnly(private val currentTime: Instant) : Criterion {
    override fun getFilter(): Filter {
        return Filter("publishDate", EQUALS_AND_GREATER_THAN, currentTime)
    }

    override fun applyPredicateFor(item: ScheduledItem): Boolean {
        val parse = getFilter()

        if (parse.key == "publishDate" && parse.predicate == EQUALS_AND_GREATER_THAN) {
            val date = parse.value as Instant
            return item.publishDate >= date
        }

        TODO()
    }
}
