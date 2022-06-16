package persistence

import socialPosts.ScheduledItem
import adapters.outbound.persistence.SchedulerRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import socialPosts.SocialPosts
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant

class FileSystemSchedulerRepository(private val filePath: String) : SchedulerRepository {
    override fun save(scheduledItem: ScheduledItem): Boolean {
        val file = File(filePath)

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        printer.printRecord(scheduledItem.post.id, scheduledItem.publishDate)
        printer.close()

        return true
    }

    override fun findAll(): ArrayList<ScheduledItem> {
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
        return ScheduledItem(SocialPosts(record[0].toInt(), ""), Instant.parse(record[1]))
    }
}