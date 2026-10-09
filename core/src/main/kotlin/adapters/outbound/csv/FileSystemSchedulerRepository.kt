package adapters.outbound.csv

import application.entities.ScheduledItem
import application.entities.SocialMedia
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import application.persistence.configuration.ConfigurationRepository
import application.scheduler.filters.Criterion
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant

private const val POST_ID_INDEX = 0
private const val PUBLISH_DATE_INDEX = 1
private const val SCHEDULE_ITEM_ID_INDEX = 2
private const val IS_PUBLISHED_INDEX = 3
private const val SOCIAL_MEDIA_INDEX = 4

class FileSystemSchedulerRepository(
    private val filePath: String = StorePath.DEFAULT.file("scheduler-${DataFileSuffix.DEFAULT_FALLBACK}.csv"),
    private val postsRepository: PostsRepository,
    private val configurationRepository: ConfigurationRepository? = null,
    private val storePath: StorePath = StorePath.DEFAULT,
) : SchedulerRepository {
    override fun save(scheduledItem: ScheduledItem): Boolean {
        val file = File(currentFile())
        file.parentFile?.mkdirs()

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        val nextId =
            if (scheduledItem.id.isNullOrEmpty()) {
                (findAll().size + PUBLISH_DATE_INDEX).toString()
            } else {
                scheduledItem.id
            }

        printer.printRecord(
            scheduledItem.post.id,
            scheduledItem.publishDate,
            nextId,
            scheduledItem.published.toString(),
            scheduledItem.socialMedia.name,
        )
        printer.close()

        return true
    }

    override fun findAll(filters: ArrayList<Criterion>): ArrayList<ScheduledItem> {
        ensureFileExists(File(currentFile()))

        val reader = FileReader(currentFile())
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        var scheduledItems = arrayListOf<ScheduledItem>()

        for (record in parser) {
            val element = buildPostFromCsvRecord(record)
            scheduledItems.add(element)
        }

        parser.close()

        val iterator = filters.iterator()

        while (iterator.hasNext()) {
            val criterion = iterator.next()
            scheduledItems = scheduledItems.filter { criterion.applyPredicateFor(it) } as ArrayList<ScheduledItem>
        }

        return scheduledItems
    }

    override fun deleteById(id: String): ScheduledItem? {
        val toBeDeleted = findAll().find { it.id == id }
        val filterOutScheduledItem =
            findAll().filter {
                it.id != id
            }

        val file = File(currentFile())
        file.delete()

        filterOutScheduledItem.forEach {
            save(it)
        }

        return toBeDeleted
    }

    override fun markAsSent(scheduledItem: ScheduledItem): ScheduledItem {
        val deleted = deleteById(scheduledItem.id!!)

        deleted!!.published = true

        save(deleted)

        return deleted
    }

    private fun currentFile(): String {
        if (configurationRepository == null) {
            return filePath
        }

        return storePath.file("scheduler-${DataFileSuffix(configurationRepository).value()}.csv")
    }

    private fun buildPostFromCsvRecord(record: CSVRecord): ScheduledItem {
        val postId = record[POST_ID_INDEX]
        val socialPost = postsRepository.findById(postId)
        val publishDate = record[PUBLISH_DATE_INDEX]
        val scheduleId = record[SCHEDULE_ITEM_ID_INDEX]
        val isPublished = record[IS_PUBLISHED_INDEX]
        return ScheduledItem(
            socialPost!!,
            Instant.parse(publishDate),
            scheduleId,
            isPublished.equals("true", ignoreCase = true),
            socialMediaOf(record),
        )
    }

    private fun socialMediaOf(record: CSVRecord): SocialMedia {
        if (record.size() <= SOCIAL_MEDIA_INDEX) {
            return SocialMedia.TWITTER
        }
        val value = record[SOCIAL_MEDIA_INDEX].trim()
        return SocialMedia.entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: SocialMedia.TWITTER
    }

    private fun ensureFileExists(file: File) {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
