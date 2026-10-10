package application.scheduler

import application.Messages
import application.Output
import application.Timezone
import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.filters.DateTimeValidation
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException
import java.time.temporal.WeekFields
import kotlin.random.Random

class PickRandom(
    private val postsRepository: PostsRepository,
    private val scheduleRepository: SchedulerRepository,
    private val configurationRepository: ConfigurationRepository,
    private val cliOutput: Output,
    private val now: () -> Instant = { Instant.now() },
    private val random: Random = Random.Default,
) {
    fun invoke(
        targetDate: String,
        socialMedia: SocialMedia = SocialMedia.TWITTER,
    ): String {
        if (targetDate.isEmpty()) {
            return cliOutput.write(Messages.MISSING_REQUIRED_FIELDS)
        }

        val timezone = Timezone.configured(configurationRepository)
        val zone = Timezone.zoneId(timezone)
        val requestedDay = parseDay(targetDate, zone) ?: return cliOutput.write(INVALID_DATE)

        val day = maxOf(requestedDay, now().atZone(zone).toLocalDate())

        val publishDate =
            randomInstant(day, zone) ?: return cliOutput.write("No available time to schedule a post on $targetDate")

        val candidates = availablePosts(day, socialMedia, zone)
        if (candidates.isEmpty()) {
            return cliOutput.write("No post available to schedule on the week of $day")
        }

        val post = candidates[random.nextInt(candidates.size)]
        scheduleRepository.save(ScheduledItem(post, publishDate, socialMedia = socialMedia))
        return cliOutput.write("Post ${post.id} has been randomly scheduled for $publishDate using $timezone timezone")
    }

    private fun randomInstant(
        day: LocalDate,
        zone: ZoneId,
    ): Instant? {
        val startOfDay = day.atStartOfDay(zone).toInstant()
        val endOfDay = day.plusDays(1).atStartOfDay(zone).toInstant()
        val earliest = maxOf(startOfDay, now().plus(MINIMUM_DELAY))
        if (earliest.epochSecond >= endOfDay.epochSecond) {
            return null
        }

        return Instant.ofEpochSecond(random.nextLong(earliest.epochSecond, endOfDay.epochSecond))
    }

    private fun availablePosts(
        day: LocalDate,
        socialMedia: SocialMedia,
        zone: ZoneId,
    ): kotlin.collections.List<SocialPosts> {
        val weekStart = day.with(WeekFields.ISO.dayOfWeek(), 1)
        val weekEnd = weekStart.plusWeeks(1)

        val scheduledThisWeek =
            scheduleRepository
                .findAll()
                .filter { it.socialMedia == socialMedia }
                .filter {
                    val scheduledDay = it.publishDate.atZone(zone).toLocalDate()
                    !scheduledDay.isBefore(weekStart) && scheduledDay.isBefore(weekEnd)
                }.mapNotNull { it.post.id }
                .toSet()

        return postsRepository.findAll().filter { it.id.orEmpty() !in scheduledThisWeek }
    }

    private fun parseDay(
        raw: String,
        zone: ZoneId,
    ): LocalDate? {
        try {
            return LocalDate.parse(raw)
        } catch (_: DateTimeParseException) {
            // fall back to a full instant or local date time
        }

        val validation = DateTimeValidation(raw, zone)
        return if (validation.isDateTimeValid()) {
            validation.value().atZone(zone).toLocalDate()
        } else {
            null
        }
    }

    companion object {
        const val INVALID_DATE = "Invalid date time to schedule post"
        val MINIMUM_DELAY: Duration = Duration.ofMinutes(30)
    }
}
