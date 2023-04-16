package application.scheduler

import application.Messages
import application.Output
import application.entities.ScheduledItem
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.filters.DateTimeValidation

class Create(
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val cliOutput: Output
) {

    fun invoke(postId: String, targetDate: String): String {
        if (postId.isNotEmpty() && targetDate.isNotEmpty()) {
            val post = postsRepository.findById(postId) ?: return cliOutput.write("Couldn't find post with id $postId")

            val validTargetDate = DateTimeValidation(targetDate)

            if (!validTargetDate.isDateTimeValid()) {
                return cliOutput.write("Invalid date time to schedule post")
            }

            scheduleRepository.findAll().forEach {
                if (it.publishDate.toString() == targetDate && it.post.id == postId) {
                    return cliOutput.write("Post is already scheduled for $targetDate")
                }
            }

            scheduleRepository.save(
                ScheduledItem(
                    post, validTargetDate.value()
                )
            )
            val configuration = configurationRepository.find()
            return cliOutput.write("Post has been scheduled using ${configuration.timezone} timezone")
        }

        return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
    }
}
