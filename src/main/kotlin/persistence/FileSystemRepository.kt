package persistence

import PostsRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import socialPosts.SocialPosts
import java.io.File
import java.io.FileReader
import java.io.FileWriter


class FileSystemRepository(private val filePath: String): PostsRepository {
    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        try {
            File(filePath).parentFile.mkdirs()
        } catch (ex: NullPointerException) {
            print(ex.message)
        }

        val file = File(filePath)

        val writer = FileWriter(file, true)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        for (post: SocialPosts in posts) {
            printer.printRecord(post.text, post.id)
        }
        printer.close()

        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
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
        return SocialPosts(record[1].toInt(), record[0])
    }
}
