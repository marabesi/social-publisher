package application.scheduler.filters

import application.entities.ScheduledItem

class PropertyFilter(
    private val property: String,
    private val expected: String,
) : Criterion {
    override fun getFilter(): Filter = Filter(property, EQUALS, expected)

    override fun applyPredicateFor(item: ScheduledItem): Boolean {
        val actual = ScheduledItemProperties(item).valueOf(property) ?: return false

        val actualNumber = actual.toLongOrNull()
        val expectedNumber = expected.toLongOrNull()

        return if (actualNumber != null && expectedNumber != null) {
            actualNumber == expectedNumber
        } else {
            actual == expected
        }
    }
}
