package adapters.outbound.csv

import application.entities.ScheduledItem
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
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

class FileSystemSchedulerRepository(
    private val filePath: String,
    private val postsRepository: PostsRepository
) : SchedulerRepository {
    override fun save(scheduledItem: ScheduledItem): Boolean {
        val file = File(filePath)

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        val nextId = if (scheduledItem.id.isNullOrEmpty()) {
            (findAll().size + PUBLISH_DATE_INDEX).toString()
        } else {
            scheduledItem.id
        }

        printer.printRecord(
            scheduledItem.post.id,
            scheduledItem.publishDate,
            nextId,
            scheduledItem.published.toString()
        )
        printer.close()

        return true
    }

    override fun findAll(filters: ArrayList<Criterion>): ArrayList<ScheduledItem> {
        ensureFileExists(File(filePath))

        val reader = FileReader(filePath)
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
        val filterOutScheduledItem = findAll().filter {
            it.id != id
        }

        val file = File(filePath)
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
            isPublished.toBooleanStrict(),
        )
    }

    private fun ensureFileExists(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
