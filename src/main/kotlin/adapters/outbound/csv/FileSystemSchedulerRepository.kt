package adapters.outbound.csv

import application.entities.ScheduledItem
import application.persistence.PostsRepository
import application.persistence.SchedulerRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant

class FileSystemSchedulerRepository(
    private val filePath: String,
    private val postsRepository: PostsRepository
) : SchedulerRepository {
    override fun save(scheduledItem: ScheduledItem): Boolean {
        val file = File(filePath)

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        val nextId = if (scheduledItem.id.isNullOrEmpty()) {
            (findAll().size + 1).toString()
        } else{
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

    override fun findAll(): ArrayList<ScheduledItem> {
        ensureFileExists(File(filePath))

        val reader = FileReader(filePath)
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        val scheduledItems = arrayListOf<ScheduledItem>()

        for (record in parser) {
            scheduledItems.add(buildPostFromCsvRecord(record))
        }

        parser.close()

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
        val postId = record[0]
        val socialPost = postsRepository.findById(postId)
        val publishDate = record[1]
        val scheduleId = record[2]
        return ScheduledItem(
            socialPost!!,
            Instant.parse(publishDate),
            scheduleId,
            record[3].toBooleanStrict(),
        )
    }

    private fun ensureFileExists(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
