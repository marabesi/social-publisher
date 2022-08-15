package application.scheduler.filters

import application.entities.ScheduledItem

data class Filter (
    val key: String,
    val predicate: String,
    val value: Any,
)

interface Criterion {
    fun getFilter(): Filter
    fun applyPredicateFor(item: ScheduledItem): Boolean
}

const val EQUALS_AND_GREATER_THAN = ">="
const val EQUALS_AND_LESS_THAN = "<="
