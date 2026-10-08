package adapters.outbound.csv

import application.entities.SocialPosts
import application.persistence.PostsRepository
import application.persistence.configuration.ConfigurationRepository
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class FileSystemPostRepository(
    private val filePath: String = StorePath.DEFAULT.file("posts-${DataFileSuffix.DEFAULT_FALLBACK}.csv"),
    private val configurationRepository: ConfigurationRepository? = null,
    private val storePath: StorePath = StorePath.DEFAULT,
) : PostsRepository {
    override fun save(posts: ArrayList<SocialPosts>): Boolean {
        val file = File(currentFile())
        file.parentFile?.mkdirs()

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
        ensureFileExists(File(currentFile()))

        val reader = FileReader(currentFile())
        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        val posts = arrayListOf<SocialPosts>()

        for (record in parser) {
            posts.add(buildPostFromCsvRecord(record))
        }

        parser.close()

        return posts
    }

    override fun findById(postId: String): SocialPosts? {
        ensureFileExists(File(currentFile()))

        val reader = FileReader(currentFile())
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

    private fun currentFile(): String {
        if (configurationRepository == null) {
            return filePath
        }

        return storePath.file("posts-${DataFileSuffix(configurationRepository).value()}.csv")
    }

    private fun buildPostFromCsvRecord(record: CSVRecord): SocialPosts = SocialPosts(record[1], record[0])

    private fun ensureFileExists(file: File) {
        file.parentFile?.mkdirs()
        if (!file.exists()) {
            file.createNewFile()
        }
    }
}
