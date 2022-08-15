package application.scheduler.filters

data class Filter (
    val key: String,
    val predicate: String,
    val value: Any,
)

interface Criterion {
    fun apply(): Filter
}
