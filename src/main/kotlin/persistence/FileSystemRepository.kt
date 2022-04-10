package persistence

import PostsRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import socialPosts.SocialPosts
import java.io.File
import java.io.FileWriter

class FileSystemRepository(private val filePath: String): PostsRepository {
    override fun save(post: SocialPosts): Boolean {
        try {
            File(filePath).parentFile.mkdirs()
        } catch (ex: NullPointerException) {
        }

        val file = File(filePath)

        val writer = FileWriter(file)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        printer.printRecord("text")
        printer.printRecord(post)

        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        TODO("Not yet implemented")
    }
}
