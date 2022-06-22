package adapters.outbound.csv

import application.entities.ScheduledItem
import application.persistence.SchedulerRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import application.persistence.PostsRepository
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

        printer.printRecord(scheduledItem.post.id, scheduledItem.publishDate)
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

    private fun buildPostFromCsvRecord(record: CSVRecord): ScheduledItem {
        val postId = record[0]
        val socialPost = postsRepository.findById(postId)
        val publishDate = record[1]
        return ScheduledItem(socialPost!!, Instant.parse(publishDate))
    }

    private fun ensureFileExists(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}