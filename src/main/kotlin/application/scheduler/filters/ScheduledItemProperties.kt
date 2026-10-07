package application.scheduler.filters

import application.entities.ScheduledItem
import application.entities.SocialPosts
import java.time.Instant
import java.time.ZoneOffset
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class ScheduledItemProperties(
    private val item: ScheduledItem,
) {
    fun valueOf(property: String): String? = all()[property]

    fun keys(): Set<String> = all().keys

    private fun all(): Map<String, String> {
        val properties = mutableMapOf<String, String>()
        collect("", item, properties)

        val date = item.publishDate.atZone(ZoneOffset.UTC)
        properties["year"] = date.year.toString()
        properties["month"] = date.monthValue.toString()
        properties["day"] = date.dayOfMonth.toString()

        return properties
    }

    private fun collect(
        prefix: String,
        value: Any?,
        into: MutableMap<String, String>,
    ) {
        when (value) {
            null -> Unit
            is Instant -> into[prefix] = value.toString()
            is String, is Number, is Boolean -> into[prefix] = value.toString()
            else ->
                value::class.memberProperties.forEach { property ->
                    property.isAccessible = true
                    val name = if (prefix.isEmpty()) property.name else "$prefix.${property.name}"
                    collect(name, property.getter.call(value), into)
                }
        }
    }

    companion object {
        private val PROBE =
            ScheduledItem(
                SocialPosts(id = "", text = "", socialMediaId = ""),
                Instant.EPOCH,
                "",
                false,
            )

        private val AVAILABLE_KEYS: Set<String> = ScheduledItemProperties(PROBE).keys()

        fun availableKeys(): Set<String> = AVAILABLE_KEYS
    }
}
