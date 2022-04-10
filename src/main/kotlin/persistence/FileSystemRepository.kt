package persistence

import PostsRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
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

        val writer = FileWriter(file)
        val printer = CSVPrinter(writer, CSVFormat.DEFAULT)

        for (post: SocialPosts in posts) {
            printer.printRecord(post.text)
        }
        printer.close()

        return true
    }

    override fun findAll(): ArrayList<SocialPosts> {
        val reader = FileReader(filePath)
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        val posts = arrayListOf<SocialPosts>()

        for (record in parser) {
            posts.add(SocialPosts(record[0]))
        }

        parser.close()

        return posts
    }
}
