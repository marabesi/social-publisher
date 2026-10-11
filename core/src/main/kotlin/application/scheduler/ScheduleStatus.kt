package application.scheduler

enum class ScheduleStatus(
    val published: Boolean,
) {
    UPCOMING(false),
    PUBLISHED(true),
    ;

    companion object {
        fun from(value: String): ScheduleStatus? = entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) }
    }
}
