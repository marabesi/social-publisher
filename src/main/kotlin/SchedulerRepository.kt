interface SchedulerRepository {
    fun save(scheduledItem: ScheduledItem): Boolean
    fun findAll(): ArrayList<ScheduledItem>
}
