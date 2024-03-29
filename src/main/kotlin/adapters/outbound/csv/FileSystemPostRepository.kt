package adapters.outbound.csv

import application.entities.SocialPosts
import application.persistence.PostsRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.io.FileWriter

@Suppress("TooGenericExceptionCaught", "SwallowedException")
class FileSystemPostRepository(private val filePath: String) : PostsRepository {
    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        try {
            File(filePath).parentFile.mkdirs()
        } catch (ex: NullPointerException) {
//            print(ex.message)
        }

        val file = File(filePath)

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        val nextId = findAll().size + 1

        for (post: SocialPosts in posts) {
            printer.printRecord(post.text, nextId)
        }
        printer.close()

        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        ensureFileExists(File(filePath))

        val reader = FileReader(filePath)
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        val posts = arrayListOf<SocialPosts>()

        for (record in parser) {
            posts.add(buildPostFromCsvRecord(record))
        }

        parser.close()

        return posts
    }

    override fun findById(postId: String): SocialPosts? {
        val reader = FileReader(filePath)
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        var posts: SocialPosts? = null

        for (record in parser) {
            if (record[1] == postId) {
                posts = buildPostFromCsvRecord(record)
            }
        }

        parser.close()

        return posts
    }

    private fun buildPostFromCsvRecord(record: CSVRecord): SocialPosts {
        return SocialPosts(record[1], record[0])
    }

    private fun ensureFileExists(file: File) {
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
